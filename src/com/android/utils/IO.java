package com.android.utils;

import java.io.*;

public class IO
{

	static String search="";

	public static void write(String fn, String s)
	{
		try
		{
			FileOutputStream fos=new FileOutputStream(new File(fn));
			fos.write(s.getBytes());
			fos.flush();
			fos.close();
		}
		catch (Exception e)
		{}
	}

	public static String read(String fn)
	{
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		try
		{
			FileInputStream fis=new FileInputStream(new File(fn));
			int i=0;
			while ((i = fis.read()) != -1)
			{
				baos.write(i);
			}
			fis.close();
		}
		catch (Exception e)
		{}
		return new String(baos.toByteArray());
	}

	public static boolean delete(String arg)
	{
		for (String a:arg.split(" "))
		{
			del(a);
		}
		return true;
	}

	public static void del(String fn)
	{
		File x=new File(fn);
		if (x.isDirectory())
		{
			File[] ff=x.listFiles();
			if (ff != null)
			{
				for (File f:ff)
				{
					delete(f.getAbsolutePath());
				}
			}
		}
		x.delete();
	}

	public static String classname(String fn)
	{
		String s="";
		if (fn.contains("/src/"))
		{
			s = fn.substring(fn.lastIndexOf("/src/") + "/src/".length(), fn.indexOf(".")).replace("/", ".");
		}
		else
		{
			String n=new File(fn).getName();
			s = n.substring(0, n.indexOf("."));
		}
		return s;
	}

	public static void copy(final String from, final String to)
	{
		new Thread(){
			public void run()
			{
				String name=new File(from).getName();
				Run.sh("cp -R " + from + " " + to);
				Run.sh("chmod -R 0777 " + to + "/" + name);
			}
		}.start();
	}

	public static void unzip(String from, String to)
	{
		//Unzip.main((from + " " + to).split(" "));
	}

	public static void pause(long time)
	{
		try
		{
			Thread.sleep(time);
		}
		catch (Exception e)
		{}
	}

	public static String search(String end, String dir)
	{
		search = "";
		s(end, dir);
		if (search.length() > 0)
		{
			search = search.substring(0, search.length() - 1);
		}
		return search;
	}

	public static void s(String end, String dir)
	{
		for (File f:new File(dir).listFiles())
		{
			if (f.isFile())
			{
				if (f.getName().endsWith(end) || end.isEmpty())
				{
					search += f.getAbsolutePath() + " ";
				}
			}
			else
			{
				s(end, f.getAbsolutePath());
			}
		}
	}

	public static void ss(String end, String dir)
	{
		for (File f:new File(dir).listFiles())
		{
			if (f.isFile())
			{
				if (f.getName().endsWith(end))
				{
					System.out.println(f.getAbsolutePath());
				}
			}
			else
			{
				ss(end, f.getAbsolutePath());
			}
		}
	}

	public static String classpath(String dir)
	{
		String s="";
		for (String a:search(".jar", dir).split(" "))
		{
			s += a + ":";
		}
		if (s.length() > 0)
		{
			s = s.substring(0, s.length() - 1);
		}
		return s;
	}

	public static void mkdirs(String path)
	{
		new File(path).mkdirs();
	}

	public static String classpath(String cp, String dir)
	{
		for (File f:new File(dir).listFiles())
		{
			String s=f.getAbsolutePath();
			if (f.isFile())
			{
				if (s.endsWith(".jar"))
				{
					cp += ":" + s;
				}
			}
			else
			{
				classpath(cp, s);
			}
		}
		return cp;
	}

	public static String project(String fn)
	{
		String project="";
		if (fn.contains("/src/"))
		{
			project = fn.substring(0, fn.indexOf("/src/"));
		}
		else
		{
			project = Data.dir;
		}
		return project;
	}
}
