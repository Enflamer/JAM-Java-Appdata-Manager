package jam;

import java.io.IOException;
import java.io.RandomAccessFile;

public class FileOperator {
	private String path;

    // Ёкземпл€р класса который обеспечит возможность 
    // работать с файлом
    private RandomAccessFile file;

    // говорим конструктору проинициализировать путь к файлу
    public FileOperator(String path) {
        this.path = path;
    }

    // метод демонстрирует переход на указанный символ
    public long goTo(int num) throws IOException {
        // инициализируем класс RandomAccessFile 
        // в параметры передаем путь к файлу 
        // и модификатор который говорит, что файл откроетс€ только дл€ чтени€
        file = new RandomAccessFile(path, "r");

        // переходим на num символ
        file.seek(num);

        // получаем текущее состо€ние курсора в файле
        long pointer = file.getFilePointer();
        file.close();

        return pointer;
    }

    // этот метод читает файл и выводит его содержимое
    public String read() throws IOException {
        file = new RandomAccessFile(path, "r");
        String res = "";
        int b = file.read();
        // побитово читаем символы и плюсуем их в строку
        while(b != -1){
            res = res + (char)b;
            b = file.read();
        }
        file.close();

        return res;
    }

    // читаем файл с определенного символа
    public String readFrom(int numberSymbol) throws IOException {
        // открываем файл дл€ чтени€
        file = new RandomAccessFile(path, "r");
        String res = "";

        // ставим указатель на нужный вам символ
        file.seek(numberSymbol);
        int b = file.read();

        // побитово читаем и добавл€ем символы в строку
        while(b != -1){
            res = res + (char)b;

            b = file.read();
        }
        file.close();

        return res;
    }

    // запись в файл
    public void write(String st) throws IOException {
        // открываем файл дл€ записи
        // дл€ этого указываем модификатор rw (read & write)
        // что позволит открыть файл и записать его
        file = new RandomAccessFile(path, "rw");

        // записываем строку переведенную в биты
        file.write(st.getBytes());

        // закрываем файл, после чего данные записываемые данные попадут в файл
        file.close();
    }

}
