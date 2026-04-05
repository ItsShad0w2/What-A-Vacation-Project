package com.example.what_a_vacation_project;

public class LocationsPrompts
{
    public static final String locationsStructure(String country, String startDate, String endDate, String description, long daysDifference)
    {
        return  filter(description) + "\n " +
                "Return only the text of a JSON object regarding the following structure of finding and organizing the locations for the trip:\n " +
                "You're a professional tourist guide specialized in offering locations to visit for a trip.:\n " +
                "You may suggest locations to visit in the trip in the country of " + country + " from " + startDate + " until " + endDate + "\n " +
                "You must suggest the amount of " + daysDifference + " days of the trip. Do not ignore this message.\n" +
                "You must base the trip on the user's preferences which are as followed, " + description + ". Do not ignore this message.\n " +
                "Do add in each day between two to five places which five would be the maximum amount of places to visit in a day of the trip.\n " +
                "The locations suggested must be organized and in order in a list for the specific day as followed:\n +" +
                "{\n" +
                "   \"Day1\": [{\"name\": \"Eiffel Tower\", \"description\" : \"An iron lattice tower on the Champ de Mars in Paris. Amazing for an immersive view of the city.\", \"lat\": 48.858, \"lng\": 2.29445}],\n " +
                "   \"Day2\": [{\"name\": \"Tower of Pisa\", \"description\" : \"A bell tower that is located in the city of Pisa. It is famous for its architectural shape.\", \"lat\": 43.723, \"lng\": 10.3965}]\n " +
                "}\n" +
                "Do not ignore this message. The 'name' field must contain specifically the officially recognized name of the location." + "\n" +
                "Do not include comments, activity suggestions which for example would include 'Last Stroll' and other parentheses besides the name of the location in the 'name' field. Do not ignore this message." + "\n" +
                "Notes, activity suggestions and context would move to the 'description' field. Do not ignore this message." + "\n" +
                "Ensure that every single day from Day 1 to Day " + daysDifference + " is included. Do not ignore this message and skip dates." + "\n" +
                "Remember, the entire response must be a single valid JSON object that contains lists of days containing different locations suggested.";
    }
    public static final String filter(String description)
    {
        return "Analyze the user's description which is " + description + "\n " +
               "In case that this description is entirely unrelated to travel, tourism, and trip preferences, " + "\n " +
               "you're obliged to abort and return an empty JSON object " + "\n " +
               "{}" + "\n " +
               "Completely not generate locations and other text. Do not ignore this message.";
    }
}
