package project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SaveNLoad implements Serializable {
	
	public static void save(String s, Object o) throws Exception{
		FileOutputStream fileoot = new FileOutputStream(new File(s));
		ObjectOutputStream oot = new ObjectOutputStream(fileoot);
		oot.writeObject(o);
		oot.close();
	}
	public static Object load(String filename) throws Exception {
        Object obj=null;
        
        File file = new File(filename);
        if (file.exists()) {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            obj = ois.readObject();
            ois.close();
        }   
        if(obj == null)
        	throw new Exception("Cant load file");
        
        return obj;
    
	}
}
