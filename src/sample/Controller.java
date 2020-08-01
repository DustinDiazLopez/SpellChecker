package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.web.HTMLEditor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Controller {
    List<String> es = Main.esDic;
    List<String> en = Main.enDic;

    @FXML
    private HTMLEditor txtArea;

    @FXML
    private Button runBtn;

    @FXML
    void check(ActionEvent event) {
        txtArea.setHtmlText(spellCheck(splitIntoParagraphs(txtArea.getHtmlText())));
    }

    private String spellCheck(ArrayList<String> paragraphs) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<html dir=\"ltr\"><head></head><body contenteditable=\"true\">");
        HashSet<SpellChecker.Word> errors;
        String head, tail, word;
        for (String p : paragraphs) {
            stringBuilder.append("<p>");
            errors = SpellChecker.spellCheck(p, es);
            if (errors.size() != 0) {
                for (SpellChecker.Word error : errors) {
                    head = p.substring(0, error.start);
                    tail = p.substring(error.end);
                    word = formatTextToHTML(error);
                    stringBuilder.append(head).append(word).append(tail);
                }
            } else stringBuilder.append(p);
            stringBuilder.append("</p>");
        }
        stringBuilder.append("</body></html>");
        return stringBuilder.toString();
    }

    private String formatTextToHTML(SpellChecker.Word text) {
        if (text.isEmail) {
            return String.format("<a href=\"mailto:%s\">%s</a>", text.word, text.word);
        } else if (text.isWebsite) {
            return String.format("<a href=\"%s\" target=\"_blank\">%s</a>", text.word, text.word);
        } else {
            if (text.isWarning) return String.format("<a style=\"color:%s;\">%s</a>", "#b59100", text.word);
            else return String.format("<a style=\"color:%s;\">%s</a>", "#c71010", text.word);
        }
    }

    private ArrayList<String> splitIntoParagraphs(String htmlText) {
        String[] paragraphs = htmlText.replaceAll("&nbsp;", "").split("</p>");
        ArrayList<String> ret = new ArrayList<>(paragraphs.length);
        String temp;
        for (String paragraph : paragraphs) {
            temp = removeTags(paragraph).trim();
            if (!temp.isEmpty()) ret.add(temp);
        }
        return ret;
    }

    private String removeTags(String htmlText) {
        return htmlText.replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", " ");
    }

}
