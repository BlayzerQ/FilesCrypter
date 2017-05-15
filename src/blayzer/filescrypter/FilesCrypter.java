package blayzer.filescrypter;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.io.*;

public class FilesCrypter {

    private File file;
    private BufferedReader bfr;
    private String cacheOut;

    public FilesCrypter() {
        JFrame jf = new JFrame();
        jf.setTitle("FilesCrypter");
        jf.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        jf.setSize(500, 350);
        jf.setLocationRelativeTo(null);
        jf.setResizable(false);

        JPanel pane = new JPanel();
        JPanel settings = new JPanel();
        jf.add(settings);
        jf.add(pane);

        pane.setLayout(new GridLayout(4, 1));
        settings.setLayout(new GridLayout(3, 1));

        jf.setContentPane(pane);

        JTextPane out = new JTextPane();
        JTextPane info = new JTextPane();
        JTextPane key = new JTextPane(new DefaultStyledDocument() {
            @Override
            public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                if ((getLength() + str.length()) <= 16) {
                    super.insertString(offs, str, a);
                }
                else {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        });
        JButton fileb = new JButton("Выбрать файл");
        JButton reformb = new JButton("Преобразовать");
        JButton settingsb = new JButton("Настройки");
        JButton backb = new JButton("Назад");

        SimpleAttributeSet attribs = new SimpleAttributeSet();
        StyleConstants.setAlignment(attribs, StyleConstants.ALIGN_CENTER);
        StyleConstants.setFontSize(attribs, 20);
        key.setParagraphAttributes(attribs, true);
        info.setParagraphAttributes(attribs, true);
        info.setEditable(false);
        out.setParagraphAttributes(attribs, true);
        out.setEditable(false);

        key.setText("3Z99W8t266097Eau");
        info.setText("Ключ шифрования:");
        out.setText("Файл не выбран");

        out.setBackground(Color.WHITE);
        out.setFont(new Font("Serif", Font.PLAIN, 18));
        info.setBackground(Color.WHITE);
        info.setFont(new Font("Serif", Font.PLAIN, 18));
        key.setBackground(Color.WHITE);

        fileb.setBackground(Color.LIGHT_GRAY);
        reformb.setBackground(Color.LIGHT_GRAY);
        settingsb.setBackground(Color.LIGHT_GRAY);
        backb.setBackground(Color.LIGHT_GRAY);

        pane.add(out);
        pane.add(fileb);
        pane.add(reformb);
        pane.add(settingsb);
        settings.add(info);
        settings.add(key);
        settings.add(backb);

        fileb.addActionListener(action -> {
            JFileChooser fc = new JFileChooser(System.getProperty("user.home") + "/Desktop");
            int state = fc.showDialog(null, "Открыть файл");

            if(state == JFileChooser.APPROVE_OPTION) {
                file = fc.getSelectedFile();
                out.setText("Выбран файл: " + file.getName());
            }
        });

        reformb.addActionListener(action -> {
            if(file != null) {
                if(key.getText().length() < 16) {
                    cacheOut = out.getText();
                    out.setText("Ключ должен состоять из 16 символов");
                    Toolkit.getDefaultToolkit().beep();
                } else {
                out.setText("Идет обработка. Подождите...");
                fileb.setEnabled(false);
                reformb.setEnabled(false);
                Runnable work = () -> {
                    try {
                        if(!file.getPath().endsWith(".fc")) {
                            CryptoUtils.encrypt(key.getText(), file, new File(file + ".fc"));
                            //file.delete();
                        }
                        else {
                            CryptoUtils.decrypt(key.getText(), file, new File(file.getPath().split(".fc")[0]));
                            //file.delete();
                        }
                    } catch (CryptoUtils.CryptoException e) {
                        e.printStackTrace();
                    }
                    out.setText("Обработка завершена.");
                    fileb.setEnabled(true);
                    reformb.setEnabled(true);
                };
                new Thread(work).start();
                }
            } else
                Toolkit.getDefaultToolkit().beep();
        });

        settingsb.addActionListener(action -> {
            jf.setContentPane(settings);
            jf.setVisible(true);
            cacheOut = out.getText();
        });

        backb.addActionListener(action -> {
            jf.setContentPane(pane);
            out.setText(cacheOut);
        });

        jf.setVisible(true);
    }

    public static void main(String args[]) {
        new FilesCrypter();
    }
}
