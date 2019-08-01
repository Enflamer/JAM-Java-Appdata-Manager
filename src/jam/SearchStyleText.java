package jam;

import java.io.IOException;
import java.io.RandomAccessFile;

public class FileOperator {
	private String path;

    // ��������� ������ ������� ��������� ����������� 
    // �������� � ������
    private RandomAccessFile file;

    // ������� ������������ ������������������� ���� � �����
    public FileOperator(String path) {
        this.path = path;
    }

    // ����� ������������� ������� �� ��������� ������
    public long goTo(int num) throws IOException {
        // �������������� ����� RandomAccessFile 
        // � ��������� �������� ���� � ����� 
        // � ����������� ������� �������, ��� ���� ��������� ������ ��� ������
        file = new RandomAccessFile(path, "r");

        // ��������� �� num ������
        file.seek(num);

        // �������� ������� ��������� ������� � �����
        long pointer = file.getFilePointer();
        file.close();

        return pointer;
    }

    // ���� ����� ������ ���� � ������� ��� ����������
    public String read() throws IOException {
        file = new RandomAccessFile(path, "r");
        String res = "";
        int b = file.read();
        // �������� ������ ������� � ������� �� � ������
        while(b != -1){
            res = res + (char)b;
            b = file.read();
        }
        file.close();

        return res;
    }

    // ������ ���� � ������������� �������
    public String readFrom(int numberSymbol) throws IOException {
        // ��������� ���� ��� ������
        file = new RandomAccessFile(path, "r");
        String res = "";

        // ������ ��������� �� ������ ��� ������
        file.seek(numberSymbol);
        int b = file.read();

        // �������� ������ � ��������� ������� � ������
        while(b != -1){
            res = res + (char)b;

            b = file.read();
        }
        file.close();

        return res;
    }

    // ������ � ����
    public void write(String st) throws IOException {
        // ��������� ���� ��� ������
        // ��� ����� ��������� ����������� rw (read & write)
        // ��� �������� ������� ���� � �������� ���
        file = new RandomAccessFile(path, "rw");

        // ���������� ������ ������������ � ����
        file.write(st.getBytes());

        // ��������� ����, ����� ���� ������ ������������ ������ ������� � ����
        file.close();
    }

}
