/*
@author: Jadid Alam
@date: 06-07-2024
@version: 1.0
*/
package org.example;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

@Component
public class PdfBuilder {

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");

    public void createPdf() {
        final int MAX_STUDENTS = storage.getNoOfChildren();
        Document document = new Document(PageSize.A4, 60,60,15,0);

        // font of the whole file
        BaseFont baseFont = null;
        BaseFont boldBaseFont = null;
        try {
            baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            boldBaseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }

        Font normalFont = new Font(baseFont, 11);
        Font boldFont = new Font(boldBaseFont, 11);

        try {
            // name of the file
            PdfWriter invoiceDocument = PdfWriter.getInstance(document, new FileOutputStream(storage.getPdfName()));
            document.open();

            // registry
            Paragraph registry = new Paragraph("Ofsted Registration No: " + storage.getRegistry(),normalFont);
            registry.setAlignment(Element.ALIGN_RIGHT);
            document.add(registry);

            // lsc Logo
            Image logoImage = Image.getInstance(storage.getLogoLocation());
            logoImage.scaleToFit(144, 51); // Adjust the size of the image
            document.add(logoImage);

            // date
            Paragraph date = new Paragraph(storage.getInvoiceDate());
            date.setAlignment(Element.ALIGN_RIGHT);
            date.setFont(normalFont);
            document.add(date);

            // parent name
            document.add(new Paragraph(storage.getTitle() + " " + storage.getFName() + " " + storage.getLName() ,normalFont));

            // address
            document.add(new Paragraph(storage.getAddress1() + "\n" + storage.getAddress2() + "\n" + storage.getAddress3() + "\n" + storage.getPostcode() ,normalFont));

            // dear message
            document.add(new Paragraph("\nDear "+storage.getTitle()+" "+storage.getLName(),normalFont));

            // introduction paragraph
            document.add(new Paragraph("\nThis letter is to confirm the attendance of your children to London Science College for the purpose of childcare. Registration ("+storage.getRegistry()+")",normalFont));

            // main paragraph
            document.add(new Paragraph("\nWe will be providing childcare throughout the year including term holidays.\nThe following children will be attending London Science College for childcare and are charged the following fee:\n\n",normalFont));

            // student information table
            PdfPTable studentTable = new PdfPTable(5);
            if (storage.getIsHoursThere()) {
                studentTable = new PdfPTable(5);
            } else {
                studentTable = new PdfPTable(3);
            }
            studentTable.setWidthPercentage(100);

            float tablePadding = 4;

            PdfPCell studentName = new PdfPCell(new Phrase("Name",boldFont));
            studentName.setPadding(tablePadding);
            PdfPCell studentDOB = new PdfPCell(new Phrase("D.O.B",boldFont));
            studentDOB.setPadding(tablePadding);
            PdfPCell feesPerWeek = new PdfPCell(new Phrase("Fees/Week",boldFont));
            feesPerWeek.setPadding(tablePadding);

            if (storage.getIsHoursThere()) {
                PdfPCell hoursPerWeek = new PdfPCell(new Phrase("Hours/Week", boldFont));
                hoursPerWeek.setPadding(tablePadding);
                PdfPCell pricePerHour = new PdfPCell(new Phrase("Price/Hour", boldFont));
                pricePerHour.setPadding(tablePadding);

                studentTable.addCell(studentName);
                studentTable.addCell(studentDOB);
                studentTable.addCell(hoursPerWeek);
                studentTable.addCell(pricePerHour);
                studentTable.addCell(feesPerWeek);
            }
            else {
                studentTable.addCell(studentName);
                studentTable.addCell(studentDOB);
                studentTable.addCell(feesPerWeek);
            }

            for (int row = 0; row < storage.getNoOfChildren(); row++) {
                PdfPCell nameCell = new PdfPCell(new Phrase(storage.getStudentName(row),normalFont));
                nameCell.setPadding(tablePadding);
                studentTable.addCell(nameCell);

                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
                String dob = storage.getStudentDOB(row);

                try {
                    Date dobDate = inputFormat.parse(dob);
                    String dobFormatted = outputFormat.format(dobDate);
                    PdfPCell dobCell = new PdfPCell(new Phrase(dobFormatted,normalFont));
                    dobCell.setPadding(tablePadding);
                    studentTable.addCell(dobCell);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Error in parsing date dob");
                }

                if (storage.getIsHoursThere()) {
                    PdfPCell hpwCell = new PdfPCell(new Phrase(hrFormat(storage.getStudentHPW(row)), normalFont));
                    hpwCell.setPadding(tablePadding);
                    studentTable.addCell(hpwCell);

                    PdfPCell pphCell = new PdfPCell(new Phrase("£" + formatCurrency(storage.getStudentPPW(row)), normalFont));
                    pphCell.setPadding(tablePadding);
                    studentTable.addCell(pphCell);
                }

                PdfPCell fpwCell = new PdfPCell(new Phrase("£" + formatCurrency(storage.getStudentFPW(row)),normalFont));
                fpwCell.setPadding(tablePadding);
                studentTable.addCell(fpwCell);
            }

            PdfPCell totalPrice = new PdfPCell(new Phrase("Total Price (for the given period)",boldFont));
            if (storage.getIsHoursThere())
            {
                totalPrice.setColspan(4);
            }
            else {
                totalPrice.setColspan(2);
            }
            totalPrice.setPadding(tablePadding);
            studentTable.addCell(totalPrice);

            double totalP = storage.getTotalAmount();

            PdfPCell totalPriceValue = new PdfPCell(new Phrase("£"+formatCurrency(totalP),normalFont));
            totalPriceValue.setPadding(tablePadding);
            studentTable.addCell(totalPriceValue);

            document.add(studentTable);

            // payment message
            document.add(new Paragraph("\nPlease let me know if you would like any further information.\n\n",normalFont));

            // payment table
            PdfPTable paymentTable = new PdfPTable(3);
            paymentTable.setWidthPercentage(70);
            paymentTable.setHorizontalAlignment(Element.ALIGN_LEFT);

            PdfPCell dateNameCell = new PdfPCell(new Phrase("Time Period",normalFont));
            dateNameCell.setPadding(tablePadding);
            paymentTable.addCell(dateNameCell);



            PdfPCell startDateCell = new PdfPCell(new Phrase(storage.getStartDate(),normalFont));
            startDateCell.setPadding(tablePadding);
            paymentTable.addCell(startDateCell);

            PdfPCell endDateCell = new PdfPCell(new Phrase(storage.getEndDate(),normalFont));
            endDateCell.setPadding(tablePadding);
            paymentTable.addCell(endDateCell);

            PdfPCell totalPaidCell = new PdfPCell(new Phrase("Total Paid",normalFont));
            totalPaidCell.setPadding(tablePadding);
            paymentTable.addCell(totalPaidCell);

            PdfPCell totalPaidValueCell = new PdfPCell(new Phrase("£"+ formatCurrency(totalP),normalFont));
            totalPaidValueCell.setPadding(tablePadding);
            totalPaidValueCell.setColspan(2);
            paymentTable.addCell(totalPaidValueCell);

            PdfPCell remainingBalanceCell = new PdfPCell(new Phrase("Remaining",normalFont));
            remainingBalanceCell.setPadding(tablePadding);
            paymentTable.addCell(remainingBalanceCell);

            PdfPCell remainingBalanceValueCell = new PdfPCell(new Phrase("£0.00",normalFont));
            remainingBalanceValueCell.setPadding(tablePadding);
            remainingBalanceValueCell.setColspan(2);
            paymentTable.addCell(remainingBalanceValueCell);

            document.add(paymentTable);

            // ending message
            document.add(new Paragraph("\nThank you.\nYours Sincerely, \n", normalFont));

            // signature image
            Image signatureImage = Image.getInstance(storage.getSignLocation());
            signatureImage.scaleToFit(115, 60); // Adjust the size of the image
            document.add(signatureImage);

            // name and company
            document.add(new Paragraph("Mohammad Tawofi.\nLondon Science College.\n",normalFont));

            // company address
            PdfContentByte bottomPanel = invoiceDocument.getDirectContent();
            Rectangle bottomBoxBackground = new Rectangle(0, 0, document.getPageSize().getWidth(), 60);
            bottomBoxBackground.setBackgroundColor(new BaseColor(135, 206, 235));
            bottomPanel.rectangle(bottomBoxBackground);

            Phrase companyAddress = new Phrase();
            if (storage.getRegistry().equals("EY557846"))
            {
                companyAddress = new Phrase("Suite 17 First Floor, Roding House, Barking, IG11 8NL",normalFont);
            }
            else if (storage.getRegistry().equals("2649415"))
            {
                companyAddress = new Phrase("2A Boundary House, Turner Road, HA8 6BJ",normalFont);
            }
            else
            {
                System.out.println("Invalid Registry Number");
            }
            Phrase companyEmail = new Phrase("admin@londonsciencecollege.co.uk",normalFont);
            Phrase companyNumber = new Phrase("0203 827 9447",normalFont);

            ColumnText.showTextAligned(bottomPanel, Element.ALIGN_CENTER, companyAddress, document.getPageSize().getWidth()/2, 45, 0);
            ColumnText.showTextAligned(bottomPanel, Element.ALIGN_CENTER, companyEmail, document.getPageSize().getWidth()/2, 30, 0);
            ColumnText.showTextAligned(bottomPanel, Element.ALIGN_CENTER, companyNumber, document.getPageSize().getWidth()/2, 15, 0);

            document.close();
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }

    private String formatCurrency(double amount) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(amount);
    }

    private String hrFormat(double hours) {
        if (hours == Math.floor(hours)) {
            return (int)hours + "";
        } else {
            return hours + "";
        }
    }
}


