package gbarto.ru.smeta;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Noobgam on 16.08.2016.
 */
public class FileManager
{
    Context context;
    final String APPNAME = "Smeta";
    final private String extension = ".prj";
    final int MAXSIZE = 60;
    Font[] arial = new Font[MAXSIZE];
    Font[] arial_bold = new Font[MAXSIZE];
    Font[] arial_underlined = new Font[MAXSIZE];
    private int pagenumber = 1;
    public FileManager(Context t)
    {
        this.context = t;
        try {
            BaseFont base_arial = BaseFont.createFont("assets/fonts/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            BaseFont base_arial_bold = BaseFont.createFont("assets/fonts/arialbd.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            for (int i = 1; i < MAXSIZE; ++i) {
                arial[i] = new Font(base_arial, i);
                arial_bold[i] = new Font(base_arial_bold, i);
                arial_underlined[i] = new Font(base_arial, i,Font.UNDERLINE);
            }
        }
        catch (Exception e)
        {

        }
    }


    public void Save(ProjectClass Project)
    {
        try {
            File path = new File(context.getFilesDir(), Project.name + extension);
            path.createNewFile();
            FileWriter fileWriter = new FileWriter(path);
            BufferedWriter printer = new BufferedWriter(fileWriter);
            printer.append("//This is automatically generated file, do not edit on your own.\n");
            printer.append("place&" + Project.place + '\n');
            for (Map.Entry<WorkTypeClass, ArrayList<WorkClass>> x : Project.works.entrySet()) {
                WorkTypeClass x1 = new WorkTypeClass(x.getKey());
                printer.append("Object&" + x1.toString() + '\n');
                ArrayList <WorkClass> tmp = new ArrayList<>();
                for (WorkClass y : x.getValue())
                    printer.append("Thing&" + y.toString() + '\n');
            }
            printer.append("_END_OF_FILE_");
            printer.close();
            fileWriter.close();
        }
        catch (Exception e)
        {
            Toast.makeText(context.getApplicationContext(), "Could not save file, try again.", Toast.LENGTH_SHORT).show();
        }
    }

    public ArrayList <String> Load()
    {
        try {
            File[] AllNames = context.getFilesDir().listFiles();
            ArrayList <String> tmp = new ArrayList<>();
            for (int i = 0; i < AllNames.length; ++i) {
                String s = AllNames[i].getName();
                if (s.endsWith(extension))
                    tmp.add(s.substring(0, s.length() - 4));
            }
            return tmp;
        }
        catch (Exception e) {
            return null;
        }
    }

    private JSONObject FromString(String s)
    {
        try {
            return new JSONObject(s);
        }
        catch (JSONException e) {
            return null;
        }
    }

    String normalize (String x)
    {
        StringBuilder tmp = new StringBuilder("");
        for (int i = 0; i < x.length(); ++i) {
            if (x.charAt(i) != '[' && x.charAt(i) != ']' && x.charAt(i) != '\n' && x.charAt(i) != '\r')
                tmp.append(x.charAt(i));
        }
        return tmp.toString();
    }

    public Boolean Delete(String path)
    {
        try
        {
            File file = new File(context.getFilesDir(), path + extension);
            return file.delete();
        }
        catch (Exception e)
        {
            return false;
        }
    }

    String nospaces (String x)
    {
        StringBuilder tmp = new StringBuilder("");
        for (int i = 0; i < x.length(); ++i) {
            if (x.charAt(i) != ' ')
                tmp.append(x.charAt(i));
        }
        return tmp.toString();
    }

    private WorkClass WorkFromString(String s)
    {
        String[] temp = s.split("&");
        WorkClass ans = new WorkClass();
        ans.state = Boolean.valueOf(temp[0]);
        ans.workType = Long.valueOf(temp[1]);
        String[] temp2 = nospaces(temp[2]).split(",");
        for (int i = 0; i < temp2.length; ++i) {
            String[] lmao = temp2[i].split(";");
            ans.Materials.add(new Pair(Long.valueOf(lmao[0]), Float.valueOf(lmao[1])));
        }
        temp2 = nospaces(temp[3]).split(",");
        for (int i = 0; i < temp2.length; ++i)
            ans.RealMaterials.add(Long.valueOf(temp2[i]));
        ans.price = Float.valueOf(temp[4]);
        ans.measuring = Integer.valueOf(temp[5]);
        ans.size = Float.valueOf(temp[6]);
        ans.name = new String(temp[7]);
        ans.rowID = Long.valueOf(temp[8]);
        return ans;
    }

    public ProjectClass LoadFromFile(String path)
    {
        try
        {
            File file = new File(context.getFilesDir(), path + extension);
            FileReader fileReader = new FileReader(file);
            ProjectClass Project = new ProjectClass(path);
            BufferedReader reader = new BufferedReader(fileReader);
            String s = "";
            WorkTypeClass temp = null;
            while (true)
            {
                s = reader.readLine();
                s = normalize(s);
                if (s.equals("_END_OF_FILE_"))
                    return Project;
                if (s.substring(0, 2).equals("//"))
                    continue;
                String tmp[] = s.split("&", 2);
                if (tmp[0].equals("place"))
                    Project.place = tmp[1];
                else {
                    if (tmp[0].equals("Object")) //then this is new KEY, otherwise whole string is VALUE
                    {
                        String[] crap = tmp[1].split("&", 3);
                        String name = crap[0];
                        Long rowID = Long.valueOf(crap[1]);
                        temp = new WorkTypeClass(name);
                        temp.rowID = rowID;
                    }
                    else {
                        if (!Project.contains(temp))
                            Project.put(temp, new ArrayList<WorkClass>());
                        Project.get(temp).add(WorkFromString(tmp[1]));
                    }
                }

            }
        }
        catch (Exception e)
        {
            return null;
        }
    }

    private PdfPCell LeftPadded(PdfPCell cell)
    {
        cell.setPaddingLeft(15f);
        return cell;
    }

    private PdfPCell CenteredBold(String text)
    {
        PdfPCell cell = new PdfPCell(new Phrase(text, arial_bold[12]));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
    }

    private PdfPCell Downed(PdfPCell cell)
    {
        cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
        return cell;
    }

    private PdfPCell CenteredText(String text)
    {
        PdfPCell cell = new PdfPCell(new Phrase(text, arial[12]));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
    }
    
    private PdfPCell Empty()
    {
        return CenteredText("");
    }

    private PdfPCell LeftedBold(String text)
    {
        PdfPCell cell = new PdfPCell(new Phrase(text, arial_bold[12]));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
    }

    private PdfPCell LeftedText(String text)
    {
        PdfPCell cell = new PdfPCell(new Phrase(text, arial[12]));
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
    }

    private PdfPCell RightedText(String text)
    {
        PdfPCell cell = new PdfPCell(new Phrase(text, arial[12]));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
    }

    private PdfPCell RightedBold(String text)
    {
        PdfPCell cell = new PdfPCell(new Phrase(text, arial_bold[12]));
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
    }

    private PdfPCell ColoredBegin(PdfPCell cell)
    {
        cell.setBackgroundColor(new BaseColor(context.getResources().getColor(R.color.pdf_table_worktype_begin)));
        return cell;
    }

    private PdfPCell ColoredEnd(PdfPCell cell)
    {
        cell.setBackgroundColor(new BaseColor(context.getResources().getColor(R.color.pdf_table_worktype_end)));
        return cell;
    }

    private PdfPCell ColoredSummary(PdfPCell cell)
    {
        cell.setBackgroundColor(new BaseColor(context.getResources().getColor(R.color.pdf_table_summary)));
        return cell;
    }

    private class MyPageEventHandler extends PdfPageEventHelper
    {
        @Override
        public void onStartPage(PdfWriter writer, Document document)
        {
            PdfContentByte cb = writer.getDirectContent();
            Phrase footer = new Phrase(context.getString(R.string.pdf_page_number) + " " + Integer.toString(pagenumber++), arial[12]);
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                    footer,
                    (document.right() - document.left()) / 2 + document.leftMargin(),
                    document.bottom() - 15, 0);
        }
    }


    void MakeHeader (PdfWriter writer, Document document, SettingsManager settingsManager, float padding)
    {
        PdfContentByte cb = writer.getDirectContent();

        String companyName = settingsManager.getCompanyName();
        String companyPhone = settingsManager.getCompanyPhone();
        String companyEmail = settingsManager.getCompany_Email();
        String companyAddress = settingsManager.getCompanyAddress();
        final int size = 7;
        float keep = padding;
        float padfromtop = 0;
        if (!companyName.equals(""))
        {
            Phrase footer = new Phrase(companyName, arial_underlined[12]);
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,footer,document.left() + padding,document.top() - padfromtop, 0);
            padfromtop += 18;
            keep = Math.max(keep, padding + companyName.length() * size);
        }
        if (!companyPhone.equals(""))
        {
            Phrase footer = new Phrase(companyPhone, arial_underlined[12]);
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,footer,document.left() + padding,document.top() - padfromtop, 0);
            padfromtop += 18;
            keep = Math.max(keep, padding + companyPhone.length() * size);
        }
        if (!companyEmail.equals(""))
        {
            Phrase footer = new Phrase(companyEmail, arial_underlined[12]);
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,footer,document.left() + padding,document.top() - padfromtop, 0);
            padfromtop += 18;
            keep = Math.max(keep, padding + companyEmail.length() * size);
        }
        if (!companyAddress.equals(""))
        {
            Phrase footer = new Phrase(companyAddress, arial_underlined[12]);
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,footer,document.left() + padding,document.top() - padfromtop, 0);
            keep = Math.max(keep, padding + companyAddress.length() * size);
        }

        padding = keep;
        padfromtop = 0;
        String name = settingsManager.getFirstName();
        String surname = settingsManager.getSurname();
        String naming = name;
        if (naming.equals(""))
            naming = surname;
        else {
            if (!surname.equals(""))
                naming += " " + surname;
        }
        if (!naming.equals(""))
        {
            Phrase footer = new Phrase(naming, arial_underlined[12]);
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,footer,document.left() + padding,document.top() - padfromtop, 0);
            padfromtop += 18;
        }
        String mail = settingsManager.getEmail();
        String phone = settingsManager.getPhone();
        if (!phone.equals(""))
        {
            Phrase footer = new Phrase(phone, arial_underlined[12]);
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,footer,document.left() + padding,document.top() - padfromtop, 0);
            padfromtop += 18;
        }
        if (!mail.equals(""))
        {
            Phrase footer = new Phrase(mail, arial_underlined[12]);
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,footer,document.left() + padding,document.top() - padfromtop, 0);
        }
    }

    public double getPrice(ProjectClass Project)
    {
        double ans = 0;
        DBAdapter adapter = new DBAdapter(context);
        adapter.open();
        for (Map.Entry<WorkTypeClass, ArrayList<WorkClass>> x : Project.works.entrySet())
            for (int count_works = 0; count_works < x.getValue().size(); ++count_works)
            {
                WorkClass work = x.getValue().get(count_works);
                ans += work.size * work.price;
                for (int i = 0; i < work.Materials.size(); ++i) {
                    MaterialClass material = (MaterialClass) adapter.getRow(DBAdapter.MATERIAL_TABLE, work.RealMaterials.get(i));
                    if (material.per_object < (1e-8))
                        ans += work.size * work.Materials.get(i).second * material.price;
                    else
                        ans += Math.ceil((double) work.size * work.Materials.get(i).second / material.per_object) * material.price;
                }
            }
        return ans;
    }

    public File createPDF(ProjectClass Project)
    {
        try {
            pagenumber = 1;
            if (!Environment.getExternalStorageDirectory().canWrite())
            {
                Toast.makeText(context, context.getString(R.string.app_has_no_permission_to_write), Toast.LENGTH_SHORT).show();
                return null;
            }
            DBAdapter adapter = new DBAdapter(context);
            adapter.open();
            com.itextpdf.text.Document document = new com.itextpdf.text.Document(PageSize.A4);
            File place = new File(Environment.getExternalStorageDirectory() + File.separator + APPNAME);
            if (!place.exists())
                place.mkdir();
            File file = new File(place + "/" + Project.name + ".pdf");
            if (file.exists())
                file.delete();
            file.createNewFile();
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
            writer.setPageEvent(new MyPageEventHandler());
            document.open();

            SettingsManager settingsManager = new SettingsManager(context);
            PdfPTable table = new PdfPTable(6);
            document.add(new Paragraph("\n"));
            table.setSpacingBefore(80);

            float pad = 30;
            if (settingsManager.havePhoto()) {
                Image image = Image.getInstance(settingsManager.getPhotoPath());
                if (image.getWidth() > image.getHeight()) {
                    image.scalePercent(100f / image.getWidth() * 120f);
                    image.setAbsolutePosition(document.left() - 10, document.top() - 50);
                }
                else {
                    image.scalePercent(100f / image.getHeight() * 100f);
                    image.setAbsolutePosition(document.left() - 10, document.top() - 80);
                }
                PdfContentByte canvas = writer.getDirectContentUnder();
                canvas.addImage(image);
                pad = document.left() - 10 + image.getScaledWidth() * 0.9f;
            }
            MakeHeader(writer, document, settingsManager, pad);


            String[] Headers = context.getResources().getStringArray(R.array.invoice_table);
            int[] widths = {40, 270, 50, 50, 70, 90};
            table.setWidths(widths);
            table.setPaddingTop(70f);
            table.setTotalWidth(PageSize.A4.getWidth() * 0.95f);
            table.setLockedWidth(true);
            for (int i = 0; i < Headers.length; ++i)
                table.addCell(CenteredText(Headers[i]));
            int countworks = 0;
            double work_cost = 0;
            double material_cost = 0;

            for (Map.Entry<WorkTypeClass, ArrayList<WorkClass>> x : Project.works.entrySet()) {
                table.addCell(ColoredBegin(Empty()));
                //table.addCell(CenteredText(Integer.toString(++countworks)));
                table.addCell(ColoredBegin(CenteredBold(x.getKey().name)));
                table.addCell(ColoredBegin(Empty()));
                table.addCell(ColoredBegin(Empty()));
                table.addCell(ColoredBegin(Empty()));
                table.addCell(ColoredBegin(Empty()));
                ArrayList<WorkClass> worklist = x.getValue();
                double work_type_total = 0;
                for (int count_works = 1; count_works <= worklist.size(); ++count_works) {
                    WorkClass work = worklist.get(count_works - 1);
                    table.addCell(LeftedText(Integer.toString(count_works)));
                    table.addCell(LeftedText(work.name));
                    table.addCell(CenteredText(context.getResources().getStringArray(R.array.measurements_work_short)[work.measuring]));
                    table.addCell(CenteredText(Float.toString(work.size)));
                    table.addCell(CenteredText(Float.toString(work.price)));
                    table.addCell(RightedText(Float.toString(work.price * work.size)));
                    double work_total = work.price * work.size;
                    work_cost += work_total;
                    for (int i = 0; i < work.Materials.size(); ++i) {
                        MaterialTypeClass materialTypeClass = (MaterialTypeClass) adapter.getRow(DBAdapter.MATERIAL_TYPES_TABLE, work.Materials.get(i).first);
                        MaterialClass material = (MaterialClass) adapter.getRow(DBAdapter.MATERIAL_TABLE, work.RealMaterials.get(i));
                        table.addCell(LeftedText(Integer.toString(count_works) + "." + Integer.toString(i + 1)));
                        table.addCell(LeftPadded(LeftedText(material.name)));
                        table.addCell(CenteredText(context.getResources().getStringArray(R.array.measurements_material_short)[materialTypeClass.measurement]));
                        if (material.per_object < (1e-8)) {
                            table.addCell(CenteredText(Float.toString(work.size * work.Materials.get(i).second)));
                            table.addCell(CenteredText(Float.toString(material.price)));
                            double wasted = work.size * work.Materials.get(i).second * material.price;
                            table.addCell(RightedText(Double.toString(wasted)));
                            work_total += wasted;
                            material_cost += wasted;
                        } else {
                            int amount = (int) Math.ceil((double) work.size * work.Materials.get(i).second / material.per_object);
                            table.addCell(CenteredText(Integer.toString(amount)));
                            table.addCell(CenteredText(Float.toString(material.price)));
                            double wasted = amount * material.price;
                            table.addCell(RightedText(Double.toString(wasted)));
                            work_total += wasted;
                            material_cost += wasted;
                        }
                    }
                    table.addCell(Empty());
                    table.addCell(LeftedText(context.getString(R.string.pdf_total_cost)));
                    table.addCell(Empty());
                    table.addCell(Empty());
                    table.addCell(Empty());
                    table.addCell(RightedText(Double.toString(work_total)));
                    work_type_total += work_total;
                }
                table.addCell(ColoredEnd(Empty()));
                table.addCell(ColoredEnd(LeftedBold(context.getString(R.string.pdf_total_cost))));
                table.addCell(ColoredEnd(Empty()));
                table.addCell(ColoredEnd(Empty()));
                table.addCell(ColoredEnd(Empty()));
                table.addCell(ColoredEnd(RightedBold(Double.toString(work_type_total))));
            }
            for (int i = 0; i < 6; ++i) {
                PdfPCell cell = Empty();
                cell.setMinimumHeight(12);
                table.addCell(cell);
            }
            table.addCell(ColoredSummary(Empty()));
            table.addCell(ColoredSummary(LeftedBold(context.getString(R.string.pdf_work_summary))));
            table.addCell(ColoredSummary(Empty()));
            table.addCell(ColoredSummary(Empty()));
            table.addCell(ColoredSummary(Empty()));
            table.addCell(ColoredSummary(RightedBold(Double.toString(work_cost))));

            table.addCell(ColoredSummary(Empty()));
            table.addCell(ColoredSummary(LeftedBold(context.getString(R.string.pdf_materials_summary))));
            table.addCell(ColoredSummary(Empty()));
            table.addCell(ColoredSummary(Empty()));
            table.addCell(ColoredSummary(Empty()));
            table.addCell(ColoredSummary(RightedBold(Double.toString(material_cost))));
            for (int i = 0; i < 6; ++i) {
                PdfPCell cell = Empty();
                cell.setMinimumHeight(12);
                table.addCell(cell);
            }
            table.addCell(ColoredSummary(Empty()));
            table.addCell(ColoredSummary(LeftedBold(context.getString(R.string.pdf_total_invoice))));
            table.addCell(ColoredSummary(Empty()));
            table.addCell(ColoredSummary(Empty()));
            table.addCell(ColoredSummary(Empty()));
            table.addCell(ColoredSummary(RightedBold(Double.toString(work_cost + material_cost))));

            document.add(table);
            adapter.close();
            document.close();
            settingsManager.close();
            return file;
        }
        catch (Exception e)
        {
            Toast.makeText(context.getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public void openPDF(ProjectClass Project)
    {
        try {
            if (!Environment.getExternalStorageDirectory().canWrite())
            {
                Toast.makeText(context, context.getString(R.string.app_has_no_permission_to_write), Toast.LENGTH_SHORT).show();
                return;
            }
            createPDF(Project);
            File place = new File(Environment.getExternalStorageDirectory() + File.separator + APPNAME);
            if (!place.exists())
                place.mkdir();
            File file = new File(place + "/" + Project.name + ".pdf");
            Intent x = new Intent(Intent.ACTION_VIEW);
            x.setDataAndType(Uri.fromFile(file), "application/pdf");
            x.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            x.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Intent intent = Intent.createChooser(x, "Open file");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
        catch (Exception e)
        {
            Toast.makeText(context.getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private HSSFWorkbook wb;
    private HashMap<HSSFCellStyle, HSSFCellStyle> lefted_styles = new HashMap<>(), righted_styles = new HashMap<>(), centered_styles = new HashMap<>();

    private HSSFCellStyle Lefted(HSSFCellStyle style)
    {
        if (lefted_styles.containsKey(style))
            return lefted_styles.get(style);
        HSSFCellStyle style2 = wb.createCellStyle();
        style2.setFillForegroundColor(style.getFillForegroundColor());
        style2.setFillPattern(style.getFillPattern());
        style2.setAlignment(CellStyle.ALIGN_LEFT);
        lefted_styles.put(style, style2);
        return style2;
    }

    private HSSFCellStyle Righted(HSSFCellStyle style)
    {
        if (righted_styles.containsKey(style))
            return righted_styles.get(style);
        HSSFCellStyle style2 = wb.createCellStyle();
        style2.setFillForegroundColor(style.getFillForegroundColor());
        style2.setFillPattern(style.getFillPattern());
        style2.setAlignment(CellStyle.ALIGN_RIGHT);
        righted_styles.put(style, style2);
        return style2;
    }

    private HSSFCellStyle Centered(HSSFCellStyle style)
    {
        if (centered_styles.containsKey(style))
            return centered_styles.get(style);
        HSSFCellStyle style2 = wb.createCellStyle();
        style2.setFillForegroundColor(style.getFillForegroundColor());
        style2.setFillPattern(style.getFillPattern());
        style2.setAlignment(CellStyle.ALIGN_CENTER);
        centered_styles.put(style, style2);
        return style2;
    }

    float[] xl_widths = {2.4f,0,0,0,0,0};

    private Cell newCell(Row row, int i, int cellValue, CellStyle style)
    {
        Cell c = row.createCell(i);
        c.setCellValue(cellValue);
        c.setCellStyle(style);
        xl_widths[i] = Math.max(xl_widths[i], Integer.toString(cellValue).length());
        return c;
    }

    private Cell newCell(Row row, int i, String cellValue, CellStyle style)
    {
        Cell c = row.createCell(i);
        c.setCellValue(cellValue);
        c.setCellStyle(style);
        xl_widths[i] = Math.max(xl_widths[i], cellValue.length());
        return c;
    }

    private Cell newCell(Row row, int i, double cellValue, CellStyle style)
    {
        Cell c = row.createCell(i);
        if (cellValue > 1)
            cellValue = (int)(cellValue * 100 + 0.5) * 0.01;
        c.setCellValue(Float.toString((float)cellValue));
        c.setCellStyle(style);
        xl_widths[i] = Math.max(xl_widths[i], Double.toString(cellValue).length());
        return c;
    }

    private Cell newCell(Row row, int i, CellStyle style)
    {
        Cell c = row.createCell(i);
        c.setCellStyle(style);
        return c;
    }

    private Cell newFormula(Row row, int i, String formula, CellStyle style)
    {
        Cell c = row.createCell(i);
        c.setCellFormula(formula);
        c.setCellStyle(style);
        c.setCellType(Cell.CELL_TYPE_FORMULA);
        xl_widths[i] = Math.max(xl_widths[i], 10);
        return c;
    }

    public File createXLS(ProjectClass Project)
    {
        try
        {
            lefted_styles.clear();
            centered_styles.clear();
            righted_styles.clear();
            if (!Environment.getExternalStorageDirectory().canWrite())
            {
                Toast.makeText(context, context.getString(R.string.app_has_no_permission_to_write), Toast.LENGTH_SHORT).show();
                return null;
            }
            DBAdapter adapter = new DBAdapter(context);
            adapter.open();
            File place = new File(Environment.getExternalStorageDirectory() + File.separator + APPNAME);
            if (!place.exists())
                place.mkdir();
            File file = new File(place + "/" + Project.name + ".xls");
            if (file.exists())
                file.delete();
            file.createNewFile();
            SettingsManager settingsManager = new SettingsManager(context);
            wb = new HSSFWorkbook();
            HSSFPalette palette = wb.getCustomPalette();
            short color_worktype_begin = 57;
            short color_worktype_end = 58;
            short color_summary = 59;
            int color = context.getResources().getColor(R.color.pdf_table_worktype_begin);
            palette.setColorAtIndex(color_worktype_begin, (byte)((color>>16)&255), (byte)((color>>8)&255), (byte)(color&255));
            color = context.getResources().getColor(R.color.pdf_table_worktype_end);
            palette.setColorAtIndex(color_worktype_end, (byte)((color>>16)&255), (byte)((color>>8)&255), (byte)(color&255));
            color = context.getResources().getColor(R.color.pdf_table_summary);
            palette.setColorAtIndex(color_summary, (byte)((color>>16)&255), (byte)((color>>8)&255), (byte)(color&255));
            Cell c = null;
            HSSFCellStyle csWorkBegin, csWorkEnd, csTotal, simple;
            simple = wb.createCellStyle();
            csWorkEnd = wb.createCellStyle();
            csWorkEnd.setFillForegroundColor(color_worktype_end);
            csWorkEnd.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            csWorkBegin = wb.createCellStyle();
            csWorkBegin.setFillForegroundColor(color_worktype_begin);
            csWorkBegin.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            csTotal = wb.createCellStyle();
            csTotal.setFillForegroundColor(color_summary);
            csTotal.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            String[] Headers = context.getResources().getStringArray(R.array.invoice_table);
            Sheet sheet = null;
            sheet = wb.createSheet(APPNAME);
            int rowcount = 0;
            Row row = sheet.createRow(rowcount++);
            for (int i = 0; i < Headers.length; ++i)
                newCell(row, i, Headers[i], simple);

            double material_total = 0;
            double works_total = 0;

            for (Map.Entry<WorkTypeClass, ArrayList<WorkClass>> x : Project.works.entrySet()) {
                row = sheet.createRow(rowcount++);
                int worktype_row = rowcount;
                double type_total = 0;
                newCell(row, 0, csWorkBegin);
                newCell(row, 1, x.getKey().name, Centered(csWorkBegin));
                newCell(row, 2, csWorkBegin);
                newCell(row, 3, csWorkBegin);
                newCell(row, 4, csWorkBegin);
                newCell(row, 5, csWorkBegin);
                ArrayList<WorkClass> worklist = x.getValue();
                for (int count_works = 1; count_works <= worklist.size(); ++count_works) {
                    WorkClass work = worklist.get(count_works - 1);
                    row = sheet.createRow(rowcount++);
                    double worksize = work.size;
                    newCell(row, 0, count_works, Lefted(simple));
                    newCell(row, 1, work.name, Lefted(simple));
                    newCell(row, 2, context.getResources().getStringArray(R.array.measurements_work_short)[work.measuring], Centered(simple));
                    newCell(row, 3, work.size, Centered(simple));
                    newCell(row, 4, work.price, Centered(simple));
                    double work_total = work.size * work.price;
                    works_total += work_total;
                    newCell(row, 5, work_total, Righted(simple));
                    for (int i = 0; i < work.Materials.size(); ++i) {
                        MaterialTypeClass materialTypeClass = (MaterialTypeClass) adapter.getRow(DBAdapter.MATERIAL_TYPES_TABLE, work.Materials.get(i).first);
                        MaterialClass material = (MaterialClass) adapter.getRow(DBAdapter.MATERIAL_TABLE, work.RealMaterials.get(i));
                        row = sheet.createRow(rowcount++);
                        newCell(row, 0, count_works + "." + (i + 1), simple);
                        newCell(row, 1, material.name, simple);
                        newCell(row, 2, context.getResources().getStringArray(R.array.measurements_material_short)[materialTypeClass.measurement], Centered(simple));
                        if (material.per_object < (1e-8)) {
                            newCell(row, 3, worksize * work.Materials.get(i).second, Centered(simple));
                            newCell(row, 4, material.price, Centered(simple));
                            newCell(row, 5, worksize * work.Materials.get(i).second * material.price, Righted(simple));
                            work_total += worksize * work.Materials.get(i).second * material.price;
                        } else {
                            int amount = (int) Math.ceil((double) work.size * work.Materials.get(i).second / material.per_object);
                            newCell(row, 3, amount, Centered(simple));
                            newCell(row, 4, material.price, Centered(simple));
                            newCell(row, 5, amount * material.price, Righted(simple));
                            work_total += amount * material.price;
                        }
                    }
                    row = sheet.createRow(rowcount++);
                    newCell(row, 1, context.getString(R.string.pdf_total_cost), simple);
                    newCell(row, 5, work_total, Righted(simple));
                    material_total += work_total - work.size * work.price;
                    type_total += work_total;
                }
                row = sheet.createRow(rowcount++);
                newCell(row, 0, "", csWorkEnd);
                newCell(row, 1, context.getString(R.string.pdf_total_cost), csWorkEnd);
                newCell(row, 2, "", csWorkEnd);
                newCell(row, 3, "", csWorkEnd);
                newCell(row, 4, "", csWorkEnd);
                newCell(row, 5, type_total, Righted(csWorkEnd));
            }
            sheet.createRow(rowcount++);
            row = sheet.createRow(rowcount++);
            newCell(row, 0, "", csTotal);
            newCell(row, 1, context.getString(R.string.pdf_materials_summary), csTotal);
            newCell(row, 2, "", csTotal);
            newCell(row, 3, "", csTotal);
            newCell(row, 4, "", csTotal);
            newCell(row, 5, material_total, Righted(csTotal));
            row = sheet.createRow(rowcount++);
            newCell(row, 0, "", csTotal);
            newCell(row, 1, context.getString(R.string.pdf_work_summary), csTotal);
            newCell(row, 2, "", csTotal);
            newCell(row, 3, "", csTotal);
            newCell(row, 4, "", csTotal);
            newCell(row, 5, works_total, Righted(csTotal));
            sheet.createRow(rowcount++);
            row = sheet.createRow(rowcount++);
            newCell(row, 0, "", csTotal);
            newCell(row, 1, context.getString(R.string.pdf_total_invoice), csTotal);
            newCell(row, 2, "", csTotal);
            newCell(row, 3, "", csTotal);
            newCell(row, 4, "", csTotal);
            newCell(row, 5, works_total + material_total, Righted(csTotal));
            for (int i = 0; i < 6; ++i)
                sheet.setColumnWidth(i, (int)(xl_widths[i] * 256));
            FileOutputStream os = new FileOutputStream(file);
            wb.write(os);
            os.close();
            adapter.close();
            settingsManager.close();
            return file;
        }
        catch (Exception e)
        {
            Toast.makeText(context.getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
            return null;
        }

    }

    public void openXLS(ProjectClass Project)
    {
        try {
            if (!Environment.getExternalStorageDirectory().canWrite())
            {
                Toast.makeText(context, context.getString(R.string.app_has_no_permission_to_write), Toast.LENGTH_SHORT).show();
                return;
            }
            createXLS(Project);
            File place = new File(Environment.getExternalStorageDirectory() + File.separator + APPNAME);
            if (!place.exists())
                place.mkdir();
            File file = new File(place + "/" + Project.name + ".xls");
            Intent x = new Intent(Intent.ACTION_VIEW);
            x.setDataAndType(Uri.fromFile(file), "application/vnd.ms-excel");
            x.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            x.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Intent intent = Intent.createChooser(x, "Open file");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
        catch (Exception e)
        {
            Toast.makeText(context.getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }



}
