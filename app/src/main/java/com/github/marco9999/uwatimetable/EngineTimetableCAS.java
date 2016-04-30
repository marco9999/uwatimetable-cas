package com.github.marco9999.uwatimetable;

import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Marco on 18/04/2016.
 */
public class EngineTimetableCAS extends AsyncTask<EngineTimetableCAS.UserDetails, Void, String> {

    private UtilRetainFragment utilRetainFragment;

    EngineTimetableCAS(UtilRetainFragment utilRetainFragment) {
        if (utilRetainFragment == null)
            throw new IllegalArgumentException("utilRetainFragment was null! Needs to be a valid object for callbacks.");
        this.utilRetainFragment = utilRetainFragment;
    }

    public static class UserDetails {
        String userName;
        String userPass;

        UserDetails(String userName, String userPass) {
            this.userName = userName;
            this.userPass = userPass;
        }

        void clear() {
            userName = null;
            userPass = null;
        }
    }

    //////////////////////////////
    // CAS Timetable Functions. //
    //////////////////////////////

    private final String[] CAS_JSON_DATA_FIELDS = {"subject_code", "subject_description", "location", "activityType", "day_of_week", "start_date", "start_time", "duration", "activity_code", "week_pattern"};
    private final String CAS_WEBSITE = "https://allocateplussso.webservices.uwa.edu.au/allocateplussso.aspx";
    private final String USER_AGENT = "Mozilla/5.0";

    private Map<String, List<String>> htmlHeaders = new HashMap<>();

    private final String postData1 = "SMENC=ISO-8859-1&SMLOCALE=US-EN&target=https%3A%2F%2Fallocateplussso.webservices.uwa.edu.au%2Fallocateplussso.aspx&smquerydata=&smauthreason=0&postpreservationdata=&USER=";
    // + userName
    private final String postData2 = "&PASSWORD=";
    // + userPass

    private HttpsURLConnection getBaseHttpsConn(String urlString, String method) throws Exception {
        HttpsURLConnection connection = (HttpsURLConnection) new URL(urlString).openConnection();
        connection.setInstanceFollowRedirects(false);
        connection.setUseCaches(false);
        connection.setRequestMethod(method);
        if (method.equals("POST")) {
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        }
        connection.setRequestProperty("User-Agent", USER_AGENT);
        return connection;
    }

    private Map<String, List<String>> getHtmlHeaders() {
        return htmlHeaders;
    }

    private void setHtmlHeaders(Map<String, List<String>> htmlHeaders) {
        this.htmlHeaders.putAll(htmlHeaders);
    }

    private String getCookieRequestProperty() {
        // Method to fix any non-spec reply header cookies.
        StringBuilder cookie = new StringBuilder();
        List<String> cookieList;

        // List 1.
        cookieList = htmlHeaders.get("set-cookie");
        if (cookieList != null) {
            for (String cookieString : cookieList) { // Java bug? The Http htmlHeaders map is meant to be case-insensitive, but doesn't work. Needs lowercase set-cookie key here.
                cookie.append(cookieString.split(";")[0]);
                cookie.append("; ");
            }
        }

        // List 2.
        cookieList = htmlHeaders.get("Set-Cookie");
        if (cookieList != null) {
            for (String cookieString : cookieList) { // Java bug? The Http htmlHeaders map is meant to be case-insensitive, but doesn't work. Needs uppercase Set-Cookie key here.
                cookie.append(cookieString.split(";")[0]);
                cookie.append("; ");
            }
        }

        if (cookie.length() >= 2)  {
            cookie.delete(cookie.length() - 2, cookie.length());
            return cookie.toString();
        }
        else {
            return "";
        }
    }

    private void writeHtmlPostData(HttpsURLConnection connection, String data) throws Exception {
        OutputStream wr = connection.getOutputStream();
        wr.write(data.getBytes("UTF-8"));
        wr.flush();
        wr.close();
    }

    private JSONObject getCASDataJsonObject(UserDetails userDetails) {
        try {
            HttpsURLConnection casConn = null;
            int casConnCode;

            // Part 1. Get CAS page (1).
            casConn = getBaseHttpsConn(CAS_WEBSITE, "GET");
            casConnCode = casConn.getResponseCode(); // Should get 302.
            setHtmlHeaders(casConn.getHeaderFields());
            Log.d(Tag.LOG, "Part 1 Http code = " + Integer.toString(casConnCode) + ". Expected 302.");
            Thread.sleep(250);

            // Part 2. Get SSO page.
            casConn = getBaseHttpsConn((getHtmlHeaders().get("Location").get(0)), "GET");
            casConn.setRequestProperty("Cookie", getCookieRequestProperty());
            casConnCode = casConn.getResponseCode(); // Should get 200.
            setHtmlHeaders(casConn.getHeaderFields());
            Log.d(Tag.LOG, "Part 2 Http code = " + Integer.toString(casConnCode) + ". Expected 200.");
            Thread.sleep(250);

            // Part 3. Login to SSO.
            casConn = getBaseHttpsConn((getHtmlHeaders().get("Location").get(0)), "POST");
            casConn.setRequestProperty("Cookie", getCookieRequestProperty());
            writeHtmlPostData(casConn, postData1 + userDetails.userName + postData2 + userDetails.userPass);
            casConnCode = casConn.getResponseCode(); // Should get 302.
            setHtmlHeaders(casConn.getHeaderFields());
            Log.d(Tag.LOG, "Part 3 Http code = " + Integer.toString(casConnCode) + ". Expected 302.");
            Thread.sleep(250);

            // Part 4. Get CAS page (2).
            casConn = getBaseHttpsConn((getHtmlHeaders().get("Location").get(0)), "GET");
            casConn.setRequestProperty("Cookie", getCookieRequestProperty());
            casConnCode = casConn.getResponseCode(); // Should get 302.
            setHtmlHeaders(casConn.getHeaderFields());
            Log.d(Tag.LOG, "Part 4 Http code = " + Integer.toString(casConnCode) + ". Expected 302.");
            Thread.sleep(250);

            // Part 5. Get CAS timetable page (1).
            casConn = getBaseHttpsConn((getHtmlHeaders().get("Location").get(0)), "GET");
            casConn.setRequestProperty("Cookie", getCookieRequestProperty());
            casConnCode = casConn.getResponseCode(); // Should get 200.
            setHtmlHeaders(casConn.getHeaderFields());
            Log.d(Tag.LOG, "Part 5 Http code = " + Integer.toString(casConnCode) + ". Expected 200.");
            Thread.sleep(250);

            // Part 6. Get data line, where the JSON data object will start with "data=" from the javascript.
            BufferedReader br = new BufferedReader(new InputStreamReader(casConn.getInputStream()));
            String casData = null;
            String line = br.readLine();
            while (line != null) {
                if (line.length() > 5) {
                    if (line.substring(0, 5).compareTo("data=") == 0) {
                        casData = line;
                        break;
                    }
                }
                line = br.readLine();
            }

            if (casData == null) {
                throw new RuntimeException("Cas data was null! No match was found. Check user/pass, may require debugging.");
            }

            // Part 7. Clean string to JSONObject format, by removing "data=" and trailing semi-colon.
            if (casData.length() > 5) casData = casData.substring(5, casData.length() - 1);

            Log.d(Tag.LOG, "Part 6 CAS string length = " + casData.length());

            return new JSONObject(casData);
        } catch (Exception ex) {
            Log.d(Tag.LOG, "Error occurred while getting CAS data: " + ex.getLocalizedMessage());
            return null;
        }
    }

    private HolderTimetableEntry[] getCASDataHolderObject(JSONObject jsonCasData) {
        HolderTimetableEntry[] entries;
        try {
            // Get "allocated" json object.
            JSONObject jsonCasAllocatedData = jsonCasData.getJSONObject("student").getJSONObject("allocated");

            // Allocate HolderTimetableEntry array long enough.
            entries = new HolderTimetableEntry[jsonCasAllocatedData.length()];

            // Start iterating through all of the class allocations, iterate through single class data and store in HolderTimetableEntry object.
            Iterator<String> jsonCasAllocatedDataI = jsonCasAllocatedData.keys();
            JSONObject jsonCasAllocatedEntryData;
            String[] holderEntryString;
            for (int i = 0; jsonCasAllocatedDataI.hasNext(); i++) {
                jsonCasAllocatedEntryData = jsonCasAllocatedData.getJSONObject(jsonCasAllocatedDataI.next());
                holderEntryString = new String[CAS_JSON_DATA_FIELDS.length];
                for (int j = 0; j < CAS_JSON_DATA_FIELDS.length; j++) {
                    holderEntryString[j] = jsonCasAllocatedEntryData.getString(CAS_JSON_DATA_FIELDS[j]);
                }
                entries[i] = new HolderTimetableEntry(holderEntryString, false);
            }

            // Finished.
            return entries;
        } catch (Exception ex) {
            Log.d(Tag.LOG, "Error occurred while formatting CAS data from JSON object: " + ex.getLocalizedMessage());
            return null;
        }
    }

    private void fixClassWeek(HolderTimetableEntry[] data) {
        String dayOfWeek;
        String weekPattern;
        String startDate;

        for (HolderTimetableEntry entry : data) {
            // Set values
            dayOfWeek = entry.get(ContractTimetableDatabase.COLUMN_CLASS_DAY);
            weekPattern = entry.get(ContractTimetableDatabase.COLUMN_CLASS_WEEKS);
            startDate = entry.get(ContractTimetableDatabase.COLUMN_CLASS_START_DATE);

            // Work out the int representation of dayOfWeek according to the Date object.
            List<String> dayOfWeekList = Arrays.asList("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat");
            int dayOfWeekInt = dayOfWeekList.indexOf(dayOfWeek) + 1; // Need to plus one as Java's Calendar DAY_OF_WEEK is ONE-based indexed.

            // Split up startDate into integer date[0], month[1], year[2].
            String[] startDateSplit = startDate.split("/");
            Integer[] startDateSplitInt = new Integer[startDateSplit.length];
            for (int i = 0; i < startDateSplit.length; i++) {
                startDateSplitInt[i] = Integer.parseInt(startDateSplit[i]);
            }

            // Put startDate into the Calendar object format.
            Calendar startCal = Calendar.getInstance();
            startCal.set(startDateSplitInt[2], startDateSplitInt[1] - 1, startDateSplitInt[0]); // Month is zero indexed, need to minus 1.

            // Work out the actual start date/week.
            // startDate actually holds the first day and week of the year, but the day may not be equal to the dayOfWeek. So we need to apply an offset if the day has already passed.
            // This offset will be applied to the start date block.
            int startDayOfWeekInt = startCal.get(Calendar.DAY_OF_WEEK);
            int dayOfWeekOffset = dayOfWeekInt - startDayOfWeekInt;
            // If dayOfWeekOffset is negative, it means the class will start a week later. Plus 7 to result to get correct offset.
            if (dayOfWeekOffset < 0) dayOfWeekOffset += 7;

            // Start looping through each character of the weekPattern string, where each character represents if the class is held during that week.
            // Unknown why the string length is more than 52 characters (~52 weeks in a year), but this follows the original implementation.
            // Continous 1's represent a date 'block', and start and stop where 0's are on either side.
            int dayCount = dayOfWeekOffset; // This is used to calculate the end block date, which is relative to the start block date.
            boolean inBlock = false;
            Calendar blockStart = null;
            Calendar blockEnd = null;
            ArrayList<Calendar[]> blockPairs = new ArrayList<>();
            for (int i = 0; i < weekPattern.length(); i++) {
                if (weekPattern.charAt(i) == '1') {
                    // If this is the first 1 encountered after a 0, need to start a new date block.
                    if (!inBlock) {
                        inBlock = true;
                        blockStart = (Calendar) startCal.clone();
                        blockStart.add(Calendar.DATE, dayCount);
                    }

                } else {
                    // If this is the first 0 encountered after a 1, need to end the new date block.
                    if (inBlock) {
                        inBlock = false;
                        blockEnd = (Calendar) startCal.clone();
                        blockEnd.add(Calendar.DATE, dayCount - 7); // Need to minus 7 here as the dayCount has already increased from the previous loop.
                        blockPairs.add(new Calendar[]{blockStart, blockEnd});
                    }
                }
                dayCount += 7;
            }

            // Put date blocks into their string representation: yyyy/dd/mm-yyyy/dd/mm,yyyy/dd/mm-yyyy/dd/mm or otherwise.
            StringBuilder blockString = new StringBuilder();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
            for (Calendar[] block : blockPairs) {
                blockString.append(dateFormat.format(block[0].getTime()));
                blockString.append("-");
                blockString.append(dateFormat.format(block[1].getTime()));
                blockString.append(",");
            }
            blockString.deleteCharAt(blockString.length() - 1);
            entry.put(ContractTimetableDatabase.COLUMN_CLASS_WEEKS, blockString.toString());
        }
    }

    private void fixClassDay(HolderTimetableEntry[] data) {
        final String[] casDays = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        final String[] fixedDays = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

        String oldDay;
        for (int i = 0; i < data.length; i++) {
            oldDay = data[i].get(ContractTimetableDatabase.COLUMN_CLASS_DAY);
            data[i].put(ContractTimetableDatabase.COLUMN_CLASS_DAY, fixedDays[Arrays.asList(casDays).indexOf(oldDay)]);
        }
    }

    private void fixClassEndTime(HolderTimetableEntry[] data) {
        String startTime;
        String[] startTimeSplit; // {hh, mm}
        Integer[] startTimeSplitInt; // {hh, mm}

        String endTime; // This field actually contains duration when first run through. Easy enough to convert to hh:mm. Need startTime as we need to calculate the final finish time.
        Integer endTimeInt;
        Integer endTimeHours;
        Integer endTimeMinutes;


        for (int i = 0; i < data.length; i++) {
            startTime = data[i].get(ContractTimetableDatabase.COLUMN_CLASS_START_TIME);
            endTime = data[i].get(ContractTimetableDatabase.COLUMN_CLASS_END_TIME);

            // Start by splitting the start times into hours and minutes integers.
            startTimeSplit = startTime.split(":");
            startTimeSplitInt = new Integer[]{Integer.parseInt(startTimeSplit[0]), Integer.parseInt(startTimeSplit[1])};

            // Get int result of the 'end time' (duration).
            endTimeInt = Integer.parseInt(endTime);

            // Add on end time to start time to get the real end time in hh:mm.
            endTimeMinutes = startTimeSplitInt[1] + endTimeInt;
            endTimeHours = startTimeSplitInt[0] + endTimeMinutes / 60;
            endTimeMinutes = endTimeMinutes % 60;

            // Set the end time data
            data[i].put(ContractTimetableDatabase.COLUMN_CLASS_END_TIME, String.format(Locale.US, "%02d", endTimeHours) + ":" + String.format(Locale.US, "%02d", endTimeMinutes));
        }
    }

    //////////////////////////
    // ASyncTask Functions. //
    //////////////////////////

    private void DEBUG_printData(HolderTimetableEntry[] data) {
        for (int i = 0; i < data.length; i++) {
            Log.d(Tag.LOG, "Data object " + Integer.toString(i) + ":");
            for (int j = 0; j < ContractTimetableDatabase.SET_COLUMN_NAMES.length; j++) {
                Log.d(Tag.LOG, ContractTimetableDatabase.SET_COLUMN_NAMES[j] + " : " + data[i].get(ContractTimetableDatabase.SET_COLUMN_NAMES[j]));
            }
            Log.d(Tag.LOG, "");
        }
    }

    @Override
    protected void onPreExecute() {
        // Show progress dialog.
        ProgressDialogEngineTimetableCAS dialog = new ProgressDialogEngineTimetableCAS();
        dialog.show(utilRetainFragment.getActivity().getSupportFragmentManager(), Tag.Fragment.PROGRESS_DIALOG_READFROMCAS);
    }

    @Override
    protected String doInBackground(EngineTimetableCAS.UserDetails[] params) {
        // Get user details.
        if (params == null || params.length != 1) throw new IllegalArgumentException("params.length not equal to 1 or was null.");

        // If all is ok, continue with valid user data.
        JSONObject jsonCasData = getCASDataJsonObject(params[0]);

        if (jsonCasData == null) {
            return "Error getting data. Check credentials or UWA may be having issues.";
        }

        // Set params to null (to be GC collected) as apparently it is referenced by Android OS even after this function has finished???
        // I noticed this from doing a memory heap dump... May need to be investigated more. It is referenced by mParams field in AsyncTask.
        // Because params hold the users name and password I feel like I should do this from a security standpoint.
        params[0].clear();
        params[0] = null;
        params = null;

        // Format results into valid HolderTimetableEntry objects.
        HolderTimetableEntry[] holderCasData = getCASDataHolderObject(jsonCasData);

        // Fix up some data fields, such as class_day, class_end_time and class_weeks.
        fixClassWeek(holderCasData);
        fixClassDay(holderCasData);
        fixClassEndTime(holderCasData);

        // Write results to database.
        boolean hasWritten = utilRetainFragment.getUtilFragment().getHelperTimetableDatabase().writeTimetableDBEntryArray(holderCasData);
        if (!hasWritten) {
            DEBUG_printData(holderCasData);
            throw new RuntimeException("One or more entries failed to write to database. Check logs; requires debugging!");
        }

        return "Successfully read CAS data (" + holderCasData.length + " entries).";
    }

    @Override
    protected void onPostExecute(String param) {
        // Dismiss progress dialog.
        ProgressDialogEngineTimetableCAS dialog = (ProgressDialogEngineTimetableCAS) utilRetainFragment.getActivity().getSupportFragmentManager().findFragmentByTag(Tag.Fragment.PROGRESS_DIALOG_READFROMCAS);
        if (dialog != null) dialog.dismiss();

        // Issue callback to UI thread.
        utilRetainFragment.getUtilFragment().findFragmentTimetable().getDatabaseEntriesArrayAndNotify();

        // Show Snackbar message to user.
        Snackbar.make(utilRetainFragment.getActivity().findViewById(R.id.coordinatorlayout), param, Snackbar.LENGTH_LONG).show();
    }

}
