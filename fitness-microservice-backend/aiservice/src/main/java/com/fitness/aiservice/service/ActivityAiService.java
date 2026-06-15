package com.fitness.aiservice.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActivityAiService {
    private final GeminiService geminiService;

    public Recommendation generateRecommendation(Activity activity){
        String prompt= createPromptForActivity(activity);
        String aiResponse=geminiService.getRecommendations(prompt);
        log.info("RESPONSE FROM AI {} " ,aiResponse);
        return processAIResponse(activity,aiResponse);
    }

    private Recommendation processAIResponse(Activity activity, String aiResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(aiResponse);
            JsonNode textNode = rootNode.path("candidates")
                    .get(0)
                    .path("content")
                    .get("parts")
                    .get(0)
                    .path("text");

            String jsonContent = textNode.asText()
                    .replace("```json", "")
                    .replace("```", "")
                    .trim();

            System.out.println("CLEAN JSON = ");
            System.out.println(jsonContent);
            //log.info("RESPONSE FROM CLEAN AI {} " ,jsonContent);
            JsonNode analysisJson = mapper.readTree(jsonContent);
            JsonNode analysisNode = analysisJson.path("analysis");
            StringBuilder fullAnalysis=new StringBuilder();
            addAnalysisSection(fullAnalysis,analysisNode,"overall","Overall:");
            addAnalysisSection(fullAnalysis,analysisNode,"pace","Pace:");
            addAnalysisSection(fullAnalysis,analysisNode,"heartRate","Heart Rate:");
            addAnalysisSection(fullAnalysis,analysisNode,"caloriesBurned","Calories Burned:");

            List<String> improvements=extractImprovements(analysisJson.path("improvements"));
            List<String> suggestions=extractNextWorkout(analysisJson.path("suggestions"));
            List<String> safety=extractSafetyGuidelines(analysisJson.path("safety"));

            return Recommendation.builder()
                    .activityId(activity.getId())
                    .userId(activity.getUserId())
                    .type(activity.getType().toString())
                    .recommendation(fullAnalysis.toString().trim())
                    .improvements(improvements)
                    .suggestions(suggestions)
                    .safety(safety)
                    .createdAt(LocalDateTime.now())
                    .build();

        }catch (Exception e){
            e.printStackTrace();
            return createDefaultRecommendation(activity);

        }
    }

    private Recommendation createDefaultRecommendation(Activity activity) {
        return Recommendation.builder()
                .activityId(activity.getId())
                .userId(activity.getUserId())
                .type(activity.getType().toString())
                .recommendation("Unable to generate detailed analysis")
                .improvements(Collections.singletonList("Continue With your current routine"))
                .suggestions(Collections.singletonList("Consider Consulting a fitness consultant"))
                .safety(Arrays.asList(
                        "Always Warm Up before Exercise",
                        "Stay Hydrated",
                        "Listen to your body"
                ))
                .createdAt(LocalDateTime.now())
                .build();
    }

    private List<String> extractSafetyGuidelines(JsonNode safetyNode) {
        List<String> safety=new ArrayList<>();

        if(safetyNode.isArray()){
            safetyNode.forEach(item->safety.add(item.asText()));
        }
        return safety.isEmpty() ?
                Collections.singletonList("Follow general safety guidelines") :
                safety;
    }

    private List<String> extractNextWorkout(JsonNode nextWorkoutNode) {

        List<String> suggestions = new ArrayList<>();

        if (!nextWorkoutNode.isMissingNode()) {

            String type = nextWorkoutNode.path("type").asText();
            String duration = nextWorkoutNode.path("duration").asText();
            String intensity = nextWorkoutNode.path("intensity").asText();
            String description = nextWorkoutNode.path("description").asText();

            suggestions.add("Workout Type: " + type);
            suggestions.add("Duration: " + duration);
            suggestions.add("Intensity: " + intensity);
            suggestions.add("Description: " + description);
        }

        return suggestions.isEmpty()
                ? Collections.singletonList("No workout suggestions available")
                : suggestions;
    }

    private List<String> extractImprovements(JsonNode improvementsNode) {
        List<String> improvements=new ArrayList<>();
        if(improvementsNode.isArray()){
            improvementsNode.forEach(improvement->{
                String area=improvement.path("area").asText();
                String detail=improvement.path("recommendation").asText();
                improvements.add(String.format("%s:%s",area,detail));
            });
        }
        return improvements.isEmpty() ?
                Collections.singletonList("No Specific Improvements provided") :
                improvements;
    }

    private void addAnalysisSection(StringBuilder fullAnalysis, JsonNode analysisNode, String key, String prefix) {
        if(!analysisNode.path(key).isMissingNode()){
            fullAnalysis.append(prefix)
                   .append(analysisNode.path(key).asText())
                    .append("\n\n");
        }
    }

    private String createPromptForActivity(Activity activity) {
        return String.format("""
                
                Analyze this fitness activity and provide detailed recommendations in the following EXACT JSON format:
                
                {
                  "analysis": {
                    "overall": "Overall analysis here",
                    "pace": "Pace analysis here",
                    "heartRate": "Heart rate analysis here",
                    "caloriesBurned": "Calories analysis here"
                  },
                
                  "improvements": [
                    {
                      "area": "Area name",
                      "recommendation": "Detailed recommendation",
                      "priority": "HIGH"
                    }
                  ],
                
                  "nextWorkout": {
                    "type": "Suggested workout type",
                    "duration": "45 minutes",
                    "intensity": "MEDIUM",
                    "description": "Workout description"
                  },
                
                  "nutrition": {
                    "hydration": "Hydration advice",
                    "mealSuggestion": "Meal suggestion",
                    "recoveryFood": "Recovery food recommendation"
                  },
                
                  "safety": [
                    "Safety point 1",
                    "Safety point 2"
                  ]
                }
                
                
                Analyze this activity:
                
                Activity Type: %s
                Duration: %d minutes
                Calories Burned: %d
                Additional Metrics: %s
                
                
                Provide detailed analysis focusing on:
                1. Performance quality
                2. Areas of improvement
                3. Recommended next workout
                4. Recovery suggestions
                5. Safety precautions
                
                Ensure the response follows the EXACT JSON format shown above.
                
                """,

                activity.getType(),
                activity.getDuration(),
                activity.getCaloriesBurned(),
                activity.getAdditionalMetrics()
        );
    }

}




