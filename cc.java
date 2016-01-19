import java.lang.*;
import java.util.*;
import java.io.*;
import java.net.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.nio.charset.Charset;

import java.io.*;
import javax.sound.sampled.*;

public class cc{

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        ArrayList<String> subjectnum = new ArrayList<String>();
        String snum = "";
        File f = new File("classes");
        FileInputStream fis;
        InputStreamReader isr;
        BufferedReader br;
        String line = "";


        if (f.exists() && !f.isDirectory())
        {
            try{
                fis = new FileInputStream(f); 
                isr = new InputStreamReader(fis,Charset.forName("UTF-8"));
                br = new BufferedReader(isr);
            }catch(Exception e){
                System.out.println("File reading failed :(");
                return;
            }
            try{
                while ((line = br.readLine()) != null)
                {
                    subjectnum.add(line);
                }
            }catch(Exception e)
            {
                System.out.println("Reading from the file failed :(");
                return;
            }
        }
        else{
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
            System.out.println("");
        }

        while (true)
        {
            subjectnum=classchecker(subjectnum);
            if (subjectnum == null)
            {
                return;
            }
            System.out.println("end of cycle\n");
            if (subjectnum.size() == 0)
            {
                System.out.println("No more courses to lookup!  Terminating Program...");
                return;
            }
            try{
                Thread.sleep(30*1000);
            }catch(Exception e){
                System.out.println("Wait didn't work :(");
                return;
            }
        }
    }

    public static ArrayList<String> classchecker(ArrayList<String> subjectnum)
    {
        String snum = "";
        String subject = "";
        String course = "";
        String section = "";
        String jsontext = "";
        JSONParser parser = new JSONParser();
        JSONArray array = new JSONArray();
        JSONObject json = new JSONObject();
        Object obj = new Object();
        ArrayList<String> badcourse = new ArrayList<String>();
        for (int i = 0; i < subjectnum.size(); i++)
        {
            snum = subjectnum.get(i);
            if (snum.length() != 10)
            {
                System.out.println("Invalid entry: "+snum);
                badcourse.add(snum);
                continue;
            }
            subject = snum.substring(0,3);
            course = snum.substring(4,7);
            section = snum.substring(8);
            try{
                jsontext = readUrl("http://sis.rutgers.edu/soc/courses.json?subject="+subject+"&semester=12016&campus=NB&level=U");    
            }catch (Exception e){
                System.out.println(e.getMessage());    
                return null;
            }
            try{
                obj = parser.parse(jsontext);
            }catch (Exception e){
                System.out.println(e.getMessage());
                return null;
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
                badcourse.add(snum);
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
                badcourse.add(snum);
                continue;
            }
            if ((boolean)json.get("openStatus"))
            {
                play("TP_Fairy.wav");
                System.out.println("The class "+snum+" is open! Index: "+json.get("index"));
            } else {
                System.out.println("The class "+snum+" is closed :(");
            }
        }
        for (int i = 0; i < badcourse.size(); i++)
        {
            subjectnum.remove(badcourse.get(i));
        }
        return subjectnum;

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

    public static void play(String filename)
    {
        try {
            File yourFile = new File(filename);
            AudioInputStream stream;
            AudioFormat format;
            DataLine.Info info;
            Clip clip;
        
            stream = AudioSystem.getAudioInputStream(yourFile);
            format = stream.getFormat();
            info = new DataLine.Info(Clip.class, format);
            clip = (Clip) AudioSystem.getLine(info);
            clip.open(stream);
            clip.start();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
