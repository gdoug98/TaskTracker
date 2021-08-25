package IO;

import java.io.*;

public class FileIOManager
{
    private String _filepath;
    private static FileIOManager _instance;

    private FileIOManager() { _filepath = ""; }

    public static FileIOManager getInstance()
    {
        if( _instance == null)
        {
            _instance = new FileIOManager();
        }
        return _instance;
    }

    public void setPath(String newPath)
    {
        if(newPath != null)
        {
            _filepath = newPath;
        }
    }

    public boolean writeToFile(char[] content) throws IOException
    {
        if(content.length == 0 | content == null)
        {
            return false;
        }

        FileWriter out = null;
        try
        {
            out = new FileWriter(_filepath, true);

            for (char c: content)
            {
                out.write(c);
            }
            return true;
        }
        finally
        {
            if(out != null)
            {
                out.close();
            }
        }
    }

    public boolean writeToFile(char[] content, boolean append) throws IOException
    {
        if(content.length == 0 | content == null)
        {
            return false;
        }

        FileWriter out = null;
        try
        {
            out = new FileWriter(_filepath, append);

            for (char c: content)
            {
                out.write(c);
            }
            return true;
        }
        finally
        {
            if(out != null)
            {
                out.close();
            }
        }
    }

    public String readFromFile() throws IOException
    {
        FileReader in = null;

        try
        {
            File f = new File(_filepath);
            in = new FileReader(f);
            char[] chars = new char[(int)f.length()];
            for(int i = 0; i < chars.length; i++)
            {
                chars[i] = (char)in.read();
            }

            return new String(chars);
        }
        finally
        {
            if(in != null)
            {
                in.close();
            }
        }
    }
}
