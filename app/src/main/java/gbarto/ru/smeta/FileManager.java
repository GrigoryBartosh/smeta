package gbarto.ru.smeta;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
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
    public FileManager(Context t)
    {
        this.context = t;
        try {
            BaseFont base_arial = BaseFont.createFont("assets/fonts/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            BaseFont base_arial_bold = BaseFont.createFont("assets/fonts/arialbd.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            for (int i = 1; i < MAXSIZE; ++i) {
                arial[i] = new Font(base_arial, i);
                arial_bold[i] = new Font(base_arial_bold, i);
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

    public void openPDF(ProjectClass Project)
    {
        try {
            DBAdapter adapter = new DBAdapter(context);
            adapter.open();
            com.itextpdf.text.Document document = new com.itextpdf.text.Document(PageSize.A4);
            File place = new File(Environment.getExternalStorageDirectory() + File.separator + APPNAME);
            if (!place.exists())
                place.mkdir();
            File file = new File(place + "/" + Project.name + ".pddf");
            file.createNewFile();
            File file2 = new File(place + "/" + Project.name + ".pdf");
            file2.createNewFile();
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.setMargins(0, 0, 20, 30);
            document.open();
            PdfPTable table = new PdfPTable(6);

            String[] Headers = context.getResources().getStringArray(R.array.invoice_table);
            int[] widths = {30, 320, 60, 50, 70, 80};
            table.setWidths(widths);
            table.setTotalWidth(PageSize.A4.getWidth() * 0.95f);
            table.setLockedWidth(true);
            for (int i = 0; i < Headers.length; ++i)
                table.addCell(CenteredText(Headers[i]));
            int countworks = 0;
            double work_cost = 0;
            double material_cost = 0;

            for (int iterations = 0; iterations < 5; ++iterations)
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
                    table.addCell(CenteredText(Integer.toString(count_works)));
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
                        table.addCell(CenteredText(Integer.toString(count_works) + "." + Integer.toString(i + 1)));
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
            PdfReader reader = new PdfReader(file.getAbsolutePath());
            int n = reader.getNumberOfPages();
            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(file2));
            PdfContentByte pagecontent;
            for (int i = 0; i < n; ) {
                pagecontent = stamper.getOverContent(++i);
                ColumnText.showTextAligned(pagecontent, Element.ALIGN_RIGHT,
                        new Phrase(String.format(context.getString(R.string.pdf_page_number, null), i, n), arial[12]), PageSize.A4.getWidth() * 0.97f, 15, 0);
            }
            stamper.close();
            reader.close();
            file.delete();



            Intent x = new Intent(Intent.ACTION_VIEW);
            x.setDataAndType(Uri.fromFile(file2), "application/pdf");
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
        //File file = new File(context.getFilesDir(), path + extension);
        //FileReader fileReader = new FileReader(file);
        //PdfRenderer pdfRenderer = new PdfRenderer(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_WRITE));
    }
}
