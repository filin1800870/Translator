package com.android.utils;
import android.os.*;
import com.android.translator.*;
import java.io.*;
import java.lang.Process;

public class Run extends Thread implements Runnable
{
	RunActivity ra;

	public Run(RunActivity ra)
	{
		this.ra = ra;
	}

	public void run()
	{
		Data.run = true;
		String t=Translator.translate(Data.lang, Data.fn);
		if (!t.isEmpty())
		{
			print(t);
		}
		Data.run = false;
	}

	public void print(String str)
	{
		try
		{
			ra.handler.sendMessage(Message.obtain(ra.handler, 0, str));
			sleep(20);
		}
		catch (Exception e)
		{}
	}

	public static String sh(String cmd)
	{
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		try
		{
			Process p=Runtime.getRuntime().exec("/system/bin/sh");
			InputStream out,err;
			OutputStream in;
			out = p.getInputStream();
			err = p.getErrorStream();
			in = p.getOutputStream();
			in.write((cmd + "\n").getBytes());
			in.write("exit\n".getBytes());
			in.flush();
			p.waitFor();
			int i=0;
			while ((i = out.read()) != -1)
			{
				baos.write(i);
			}
			while ((i = err.read()) != -1)
			{
				baos.write(i);
			}
		}
		catch (Exception e)
		{
			try
			{
				baos.write(e.toString().getBytes());
			}
			catch (Exception ex)
			{}
		}
		return new String(baos.toByteArray());
	}
}
