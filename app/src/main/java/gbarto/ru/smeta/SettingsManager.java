package gbarto.ru.smeta;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Григорий on 17.08.2016.
 */
public class SettingsManager {
    Context context;

    static final private String STRING_END = ":END";
    static final private String SETTINGS = "settings.txt";
    static final private String PHOTO = "photo.JPEG";

    private String first_name = "";
    private String surname = "";
    private String phone = "";
    private String email = "";
    private String company_name = "";
    private String company_phone = "";
    private String company_email = "";
    private String company_address = "";
    private Boolean have_photo = false;
    private Bitmap photo = null;

    public SettingsManager(Context t) {
        context = t;
        loadSettings();
    }

    public void close(){
        saveSettings();
    }

    public String getFirstName(){ return first_name; }
    public String getSurname(){ return surname; }
    public String getPhone(){ return phone; }
    public String getEmail(){ return email; }
    public String getCompanyName(){ return company_name; }
    public String getCompanyPhone(){ return company_phone; }
    public String getCompany_Email(){ return company_email; }
    public String getCompanyAddress(){ return company_address; }
    public Boolean havePhoto() { return have_photo; }
    public Bitmap getPhoto() { return photo; }

    public void setFirstName(String s){ first_name = s; }
    public void setSurname(String s){ surname = s; }
    public void setPhone(String s){ phone = s; }
    public void setEmail(String s){ email = s; }
    public void setCompanyName(String s){ company_name = s; }
    public void setCompanyPhone(String s){ company_phone = s; }
    public void setCompany_Email(String s){ company_email = s; }
    public void setCompanyAddress(String s){ company_address = s; }
    public void setPhoto(Bitmap b) { photo = b; have_photo = true; }
    public void deletePhoto() { photo = null; have_photo = false; }

    private void loadSettings() {
        try {
            File file = new File(context.getFilesDir(), SETTINGS);
            FileReader fileReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(fileReader);
            String s = "";

            s = reader.readLine(); first_name       = s.substring(0, s.length() - 4);
            s = reader.readLine(); surname          = s.substring(0, s.length() - 4);
            s = reader.readLine(); phone            = s.substring(0, s.length() - 4);
            s = reader.readLine(); email            = s.substring(0, s.length() - 4);
            s = reader.readLine(); company_name     = s.substring(0, s.length() - 4);
            s = reader.readLine(); company_phone    = s.substring(0, s.length() - 4);
            s = reader.readLine(); company_email    = s.substring(0, s.length() - 4);
            s = reader.readLine(); company_address  = s.substring(0, s.length() - 4);

            s = reader.readLine();
            s = s.substring(0, s.length() - 4);
            have_photo = s.equals("1");
            if (have_photo) loadPhoto();

            reader.close();
            fileReader.close();
        } catch (Exception e) {
            return;
        }
    }

    private void loadPhoto() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        File path = new File(context.getFilesDir(), PHOTO);
        photo = BitmapFactory.decodeFile(path.getPath(), options);
    }

    private void saveSettings() {
        try {
            File path = new File(context.getFilesDir(), SETTINGS);
            path.createNewFile();
            FileWriter fileWriter = new FileWriter(path);
            BufferedWriter printer = new BufferedWriter(fileWriter);

            printer.append(first_name + STRING_END + '\n');
            printer.append(surname + STRING_END + '\n');
            printer.append(phone + STRING_END + '\n');
            printer.append(email + STRING_END + '\n');
            printer.append(company_name + STRING_END + '\n');
            printer.append(company_phone + STRING_END + '\n');
            printer.append(company_email + STRING_END + '\n');
            printer.append(company_address + STRING_END + '\n');
            if (have_photo) printer.append("1" + STRING_END);
            else            printer.append("0" + STRING_END);

            if (have_photo) savePhoto();

            printer.close();
            fileWriter.close();
        } catch (Exception e) {
            return;
        }
    }

    private void savePhoto() {

        FileOutputStream out = null;
        try {
            File path = new File(context.getFilesDir(), PHOTO);
            out = new FileOutputStream(path);
            photo.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
