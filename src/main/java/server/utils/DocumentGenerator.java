package server.utils;

import client.models.BookingModel;
import client.models.UserModel;
import org.apache.poi.xwpf.usermodel.*;
import server.database.DAO.impl.TourDAOImpl;
import server.services.TourService;
import server.services.impl.TourServiceImpl;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DocumentGenerator {

    public static String generateContract(BookingModel booking, UserModel user) throws IOException {
        final TourService tourService = new TourServiceImpl(new TourDAOImpl());
        final XWPFDocument document = new XWPFDocument();

        addTitle(document);

        addParagraph(document, "Договор № " + booking.getId(), true);
        addParagraph(document, "г. Минск " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")), false);

        addParagraph(document, "Туристическая компания \"Путешествия\", именуемая в дальнейшем \"Исполнитель\", с одной стороны, и " + user.getFullName() + ", именуемый(ая) в дальнейшем \"Заказчик\", с другой стороны, заключили настоящий договор о нижеследующем:", false);

        addSectionTitle(document, "1. ПРЕДМЕТ ДОГОВОРА");
        addParagraph(document, "1.1. Исполнитель обязуется оказать Заказчику туристические услуги, а Заказчик обязуется оплатить эти услуги в порядке и на условиях, предусмотренных настоящим договором.", false);
        addParagraph(document, "1.2. Наименование тура: " + booking.getTourName(), false);
        addParagraph(document, "1.3. Даты тура: с " + tourService.getTourByName(booking.getTourName()).getStartDate() + " по " + tourService.getTourByName(booking.getTourName()).getEndDate(), false);

        addSectionTitle(document, "2. СТОИМОСТЬ УСЛУГ И ПОРЯДОК РАСЧЕТОВ");
        addParagraph(document, "2.1. Общая стоимость туристических услуг составляет: " + booking.getTotalPrice() + " руб.", false);
        addParagraph(document, "2.2. Оплата производится в полном объеме не позднее чем за 14 дней до начала тура.", false);

        addSectionTitle(document, "3. ОБЯЗАННОСТИ СТОРОН");
        addParagraph(document, "3.1. Исполнитель обязуется:", false);
        addListItem(document, "Обеспечить предоставление услуг в соответствии с условиями настоящего договора;");
        addListItem(document, "Обеспечить безопасность Заказчика во время оказания услуг;");
        addListItem(document, "Предоставить полную информацию о туре.");
        addParagraph(document, "3.2. Заказчик обязуется:", false);
        addListItem(document, "Своевременно оплатить услуги;");
        addListItem(document, "Соблюдать правила поведения во время тура;");
        addListItem(document, "Иметь при себе необходимые документы.");

        addSectionTitle(document, "4. ОТВЕТСТВЕННОСТЬ СТОРОН");
        addParagraph(document, "4.1. В случае неисполнения или ненадлежащего исполнения обязательств по настоящему договору стороны несут ответственность в соответствии с действующим законодательством Республики Беларусь.", false);

        addSectionTitle(document, "5. РЕКВИЗИТЫ И ПОДПИСИ СТОРОН");
        addParagraph(document, "ИСПОЛНИТЕЛЬ:                                                                         ЗАКАЗЧИК:", false);
        addParagraph(document, "Туристическая компания \"Путешествия\"                           " + user.getFullName(), false);
        addParagraph(document, "ИНН 1234567890, Регистрационный номер: 12345         Паспорт: ________________________", false);
        addParagraph(document, "Адрес: г. Минск, ул. Туристическая, д. 1                             Адрес: ________________________", false);
        addParagraph(document, "Тел.: +375(29)118-62-89                                                           Тел.: " + user.getPhone(), false);

        addParagraph(document, "Подпись Исполнителя: _________________                      Подпись Заказчика: _________________", false);

        Path docsPath = Paths.get("documents");
        if (!Files.exists(docsPath)) {
            Files.createDirectories(docsPath);
        }

        String fileName = "Договор_№" + booking.getId() + "_" + user.getFullName() + ".docx";
        Path filePath = docsPath.resolve(fileName);

        try (FileOutputStream out = new FileOutputStream(filePath.toFile())) {
            document.write(out);
        }

        return filePath.toString();
    }

    private static void addTitle(XWPFDocument document) {
        XWPFParagraph title = document.createParagraph();
        title.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun titleRun = title.createRun();
        titleRun.setText("ДОГОВОР НА ОКАЗАНИЕ ТУРИСТИЧЕСКИХ УСЛУГ");
        titleRun.setBold(true);
        titleRun.setFontSize(14);
    }

    private static void addSectionTitle(XWPFDocument document, String text) {
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.LEFT);
        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setBold(true);
        run.setFontSize(12);
        paragraph.setSpacingAfter(100);
    }

    private static void addParagraph(XWPFDocument document, String text, boolean bold) {
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.BOTH);
        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setBold(bold);
        paragraph.setFirstLineIndent(400);
    }

    private static void addListItem(XWPFDocument document, String text) {
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.BOTH);
        paragraph.setIndentationLeft(400);
        XWPFRun run = paragraph.createRun();
        run.setText(text);
    }
}