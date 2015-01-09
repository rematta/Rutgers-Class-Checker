import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

public class cc{

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        ArrayList<String> subjectnum = new ArrayList<String>();
        String snum = "";
        String subject = "";
        String course = "";
        String section = "";
        String jsontext = "";
        JSONParser parser = new JSONParser();
        JSONArray array = new JSONArray();
        JSONObject json = new JSONObject();
        Object obj = new Object();

        while (true)
        {
            System.out.print("Enter a subject, course number and section (e.g. 198:211:11, 000 to end): ");
            snum = scan.next();
            if (snum.equals("000"))
            {
                break;
            }
            subjectnum.add(snum);
        }

        for (int i = 0; i < subjectnum.size(); i++)
        {
            snum = subjectnum.get(i);
            subject = snum.substring(0,3);
            course = snum.substring(4,7);
            section = snum.substring(8);
            try{
                jsontext = readUrl("http://sis.rutgers.edu/soc/courses.json?subject="+subject+"&semester=12015&campus=NB&level=U");    
            }catch (Exception e){
                System.out.println(e.getMessage());    
                return;
            }
            try{
                obj = parser.parse(jsontext);
            }catch (Exception e){
                System.out.println(e.getMessage());
                return;
            }
            array = (JSONArray)obj;
            try{
                for (int j = 0;;j++)
                {
                    json = (JSONObject)array.get(j);
                    if (course.equals(json.get("courseNumber")))
                    {
                        break;
                    }
                }
            }catch (Exception e){
                System.out.println("Could not find info on the following class: "+snum);
                continue;
            }

            if (json.get("openSections") == 0)
            {
                System.out.println("No open sections for the following class: "+snum);
                continue;
            }

            array = (JSONArray)json.get("sections");
            try{
                for (int j = 0;;j++)
                {
                    json = (JSONObject)array.get(j);
                    if (section.equals(json.get("number")))
                    {
                        break;
                    }
                }
            }catch (Exception e){
                System.out.println("Could not find info(section number) on the following class: "+snum);
                continue;
            }
            if ((boolean)json.get("openStatus"))
            {
                System.out.println("\nThe class "+snum+" is open! Index: "+json.get("index")+"\n");
            } else {
                System.out.println("\nThe class "+snum+" is closed :(\n");
            }
        }
    }


    private static String readUrl(String urlString) throws Exception {
        BufferedReader reader = null;
        try {
            URL url = new URL(urlString);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer buffer = new StringBuffer();
            int read;
            char[] chars = new char[1024];
            while ((read = reader.read(chars)) != -1)
                buffer.append(chars, 0, read); 
    
            return buffer.toString();
        } finally {
            if (reader != null)
                reader.close();
        }
    }
}
