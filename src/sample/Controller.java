package sample;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Controller {
    @FXML
    private TextField tfPreStr;
    @FXML
    private TextField tfStartInt;
    @FXML
    private TextField tfIntervalInt;
    @FXML
    private TextField tfLengthInt;
    @FXML
    private Button btnConfirm;
    @FXML
    private ProgressIndicator piProgress;

    @FXML
    protected void handleSubmitButtonAction(ActionEvent event) {
        btnConfirm.setDisable(false);
        final String preStr = tfPreStr.getText();
        final String startNum = tfStartInt.getText();
        final String intervalStr = tfIntervalInt.getText();
        final String lengthStr = tfLengthInt.getText();

        try {
            Task task = createTask(btnConfirm, preStr, Integer.parseInt(startNum), Integer.parseInt(lengthStr), Integer.parseInt(intervalStr));
//            writeToFile(preStr, Integer.parseInt(startNum), Integer.parseInt(lengthStr), Integer.parseInt(intervalStr));
            piProgress.progressProperty().unbind();
            piProgress.progressProperty().bind(task.progressProperty());
            task.messageProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    System.out.println("newValue"+newValue);
                }
            });
            task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    System.out.println("success");
                }
            });
            new Thread(task).start();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("提示");
            alert.setHeaderText(null);
            alert.setContentText("请输入正确数据！");
            alert.showAndWait();
        }
    }

    public static Task createTask(final Button btnConfirm, final String pre, final int startParam, final int length, final int interval) {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                File file = new File("archive.txt");
                int start=startParam;
                if (!file.exists())
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                try {
                    BufferedWriter fileWriter = new BufferedWriter(new FileWriter(file.getName()));
                    for (int i = 0; i < length; i++) {
                        fileWriter.write(pre + start + " to " + pre + (start = start + interval));
                        fileWriter.write(System.getProperty("line.separator"));
                        start++;
                        updateMessage(""+(i + 1/ length));//messageProperty 显示是
                        updateProgress(i + 1, length);
                    }
                    fileWriter.flush();
                    fileWriter.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("提示");
                alert.setHeaderText(null);
                File outFile = new File("archive.txt");
                alert.setContentText("数据已经写入" + outFile.getAbsolutePath() + "文件");
                alert.showAndWait();
                btnConfirm.setDisable(true);

                return true;
            }
        };
    }

    public static void writeToFile(String pre, int start, int length, int interval) {
//        int start=25648;
//        int =300;
        File file = new File("archive.txt");
        if (!file.exists())
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        try {
            BufferedWriter fileWriter = new BufferedWriter(new FileWriter(file.getName()));
            for (int i = 0; i < length; i++) {
                fileWriter.write(pre + start + " to " + pre + (start = start + interval));
                fileWriter.write(System.getProperty("line.separator"));
                start++;

            }
            fileWriter.flush();
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
