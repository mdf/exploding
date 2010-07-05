
package uk.ac.horizon.ug.exploding.author;

public class FileUploadBean 
{
	private byte[] file;
	private String filename;	

	public void setFile(byte[] file) 
	{
		this.file = file;
	}

	public byte[] getFile() 
	{
		return file;
	}

	public void setFilename(String filename)
	{
		this.filename = filename;
	}

	public String getFilename()
	{
		return filename;
	}
}
