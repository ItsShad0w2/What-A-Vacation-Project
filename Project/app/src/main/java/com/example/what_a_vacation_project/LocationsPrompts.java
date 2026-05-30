package com.example.what_a_vacation_project;

public class LocationsPrompts
{
    public static String locationsStructure(String country, String startDate, String endDate, String description, long daysDifference) {
        return filter(description) + "\n " +
                "Return only the text of a JSON object regarding the following structure of finding and organizing the locations for the trip:\n " +
                "You're a professional tourist guide specialized in organizing a " + daysDifference + "-day trip strictly within the country of " + country + ".\n " +
                "Trip's dates: " + startDate + " to " + endDate + ".\n" +
                "Users' preferences to base the trip on: " + description + ".\n\n " +

                "STRICT GEOGRAPHICAL AND ROUTING RULES:\n" +
                "1. Regional Lockdown: You MUST pick one region where the trip would take place. You would pick locations that are specifically located in that region and no where else.\n" +
                "2. Dual Distance Limits: Locations visited on the SAME day MUST be within a 15 kilometer radius. When transitioning to a NEW region, the transit distance MUST STRICTLY NOT exceed 150 kilometers.\n" +
                "3. Trajectory: You must choose a single, continuous directional trajectory for the trip. For example, strictly south to north and strictly west to east. Do NOT double back on your route and zigzagging.\n" +
                "4. FATAL ERROR PREVENTION: You are explicitly forbidden from teleporting the user to world-famous capitals and tourist hubs. For example, a trip in Israel is able to not include Jerusalem. Distance overrides fame.\n" +
                "5. Locations' Variety: Assign exactly two to five locations per day to visit. Never repeat a location.\n\n" +


                "FORMATTING RULES:\n +" +
                "- The 'name' field must be specifically the officially recognized name of the location. Do NOT include parentheses and labels of 'Morning Stroll'.\n" +
                "- Put every context and activity suggestions in the 'description' field.\n" +
                "- Ensure every day from Day 1 to Day " + daysDifference + " is included\n" +

                "JSON STRUCTURE EXPECTED\n" +
                "{\n" +
                "   \"Day1\": [{\"name\": \"Eiffel Tower\", \"description\" : \"An iron lattice tower on the Champ de Mars in Paris. Amazing for an immersive view of the city.\", \"lat\": 48.858, \"lng\": 2.29445}],\n" +
                "   \"Day2\": [{\"name\": \"Palace of Versailles\", \"description\" : \"A former royal residence located near Paris, famous for its Hall of Mirrors.\", \"lat\": 48.8048, \"lng\": 2.1203}]\n" +
                "}";
    }
    public static String filter(String description)
    {
        return "Analyze the user's description which is " + description + "\n " +
               "In case that this description is unrelated to travel, tourism, and trip preferences in the slightest, " + "\n " +
               "you're obliged to abort and return an empty JSON object " + "\n " +
               "{}" + "\n " +
               "Completely not generate locations and other text. Do not ignore this message.";
    }
}
