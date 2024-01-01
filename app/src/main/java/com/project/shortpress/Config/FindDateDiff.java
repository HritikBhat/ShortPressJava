package com.project.shortpress.Config;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FindDateDiff {
    public String findDifference(String start_date)
    {
        start_date = start_date.replace('T',' ');
        start_date = start_date.replace("Z","");
        // SimpleDateFormat converts the
        // string format to date object
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf
                = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.US);

        Calendar cal = Calendar.getInstance();
        String end_date = sdf.format(cal.getTime());

        // Try Block
        try {

            // parse method is used to parse
            // the text from a string to
            // produce the date
            Date d1 = sdf.parse(start_date);
            Date d2 = sdf.parse(end_date);

            // Calculate time difference
            // in milliseconds
            long difference_In_Time
                    = d2.getTime() - d1.getTime();

            // Calculate time difference in
            // seconds, minutes, hours, years,
            // and days


            long difference_In_Minutes
                    = (difference_In_Time
                    / (1000 * 60));
            long difference_In_Hours
                    = (difference_In_Minutes
                    / (60));
            long difference_In_Days
                    = (difference_In_Hours
                    / (24));

            long difference_In_Months
                    = (difference_In_Days
                    / (30));
            long difference_In_Years
                    = (difference_In_Months
                    / 24);

            if(difference_In_Minutes<1){
                return " 1 minute ago";
            }
            if (difference_In_Minutes<120){
                return difference_In_Minutes+" minutes ago";
            }
            else if (difference_In_Minutes>120 && difference_In_Minutes<2880){
                return difference_In_Hours+" hours ago";
            }
            else if(difference_In_Minutes>2880 && difference_In_Minutes<86400){

                return difference_In_Days+" days ago";
            }
            else if(difference_In_Minutes>86400 && difference_In_Minutes<527040){
                return difference_In_Months+" months ago";
            }
            else if(difference_In_Minutes>527040){
                return difference_In_Years+" year/s ago";
            }

        }

        // Catch the Exception
        catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }
}
