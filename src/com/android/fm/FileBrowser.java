package com.android.fm;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import com.android.translator.*;
import com.android.utils.*;
import java.io.*;
import java.util.*;
import java.text.*;
import android.content.res.*;
import com.memetix.mst.language.*;

public class FileBrowser extends ListActivity implements ListView.OnItemLongClickListener
{
	String fileFolder = "";
	private File currentDir;
	private FileArrayAdapter adapter;
	EditText et;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		currentDir = new File(Data.dir);
		getListView().setOnItemLongClickListener(this);
		fill(currentDir);
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
	}

	private void fill(File f)
	{
		File[]dirs = f.listFiles();
		this.setTitle(f.getAbsolutePath());
		List<Option>dir = new ArrayList<Option>();
		List<Option>fls = new ArrayList<Option>();
		try
		{
			for (File ff: dirs)
			{
				if (ff.isDirectory())
				{
					dir.add(new Option(ff.getName(), "Folder", ff.getAbsolutePath(), ff.lastModified()));
					fileFolder = String.valueOf(ff.getName());
				}
				else
				{
					fls.add(new Option(ff.getName(), "File Size: " + getSize(ff.getAbsolutePath()) , ff.getAbsolutePath(), ff.lastModified()));
				}
			}
		}
		catch (Exception e)
		{
			Log.i("i", e.toString());
		}

		Collections.sort(dir);
		Collections.sort(fls);
		dir.addAll(fls);
		if (!Data.dir.equals("/"))
		{
			dir.add(0, new Option("..", "Parent Directory", f.getParent(), f.lastModified()));
		}
		adapter = new FileArrayAdapter(FileBrowser.this, R.layout.file_viewer, dir);
		this.setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		Option o = adapter.getItem(position);
		if (o.getData().equalsIgnoreCase("folder"))
		{
			currentDir = new File(o.getPath());
			Data.dir = currentDir.getAbsolutePath();
			String[] ff=currentDir.list();
			boolean flag=false;
			for (String f:ff)
			{
				if (f.equals("apktool.yml"))
				{
					flag = true;
					break;
				}
			}
			if (flag == true)
			{
				Data.fn = currentDir.getAbsolutePath();
				Data.dir = new File(Data.fn).getParent();
				compile(Data.fn);
			}
			else
			{
				fill(currentDir);
			}
		}
		else if (o.getData().equalsIgnoreCase("parent directory"))
		{
			currentDir = new File(Data.dir).getParentFile();
			Data.dir = currentDir.getAbsolutePath();
			fill(currentDir);
		}
		else
		{
			onFileClick(o);
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> av, View v, int position, long id)
	{
		Option o = adapter.getItem(position);
		String fn=o.getPath();
		if (fn.endsWith(".jar") || fn.endsWith(".apk") || fn.endsWith(".dex"))
		{
			functions(2, fn);
		}
		else
		{
			functions(1, fn);
		}
		return true;
	}

	public void toast(String msg)
	{
		Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
	}

	public void copyFromAssets(String from, String to)
	{
		try
		{
			AssetManager am = getAssets();
			InputStream is=am.open(from);
			OutputStream os=new FileOutputStream(to);
			byte[] buffer=new byte[1025 * 20];
			int i;
			while ((i = is.read(buffer)) != -1)
			{
				os.write(buffer, 0, i);
			}
			is.close();
			os.flush();
			os.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void onBackPressed()
	{
		if (!Data.dir.equals("/"))
		{
			currentDir = currentDir.getParentFile();
			Data.dir = currentDir.getAbsolutePath();
			fill(currentDir);
		}
	}

	private void onFileClick(Option o)
	{
		Data.fn = o.getPath();
		startActivity(new Intent(this, MainActivity.class).setAction(Intent.ACTION_VIEW).setData(Uri.fromFile(new File(Data.fn))));
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.fm, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		//Handle Item selection
		switch (item.getItemId())
		{
			case R.id.home:
				home();
				return true;
			case R.id.help:
				help();
				return true;
			case R.id.exit:
				finish();
				return true;
		}
		return false;
	}

	public boolean compile(final String dir)
	{
		final Dialog dl=new Dialog(this);
		dl.setContentView(R.layout.delete);
		dl.setTitle(getString(R.string.compile) + " " + " ?");
		Button yesBtn=(Button)dl.findViewById(R.id.yes);
		Button noBtn=(Button)dl.findViewById(R.id.no);
		yesBtn.setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View v)
				{
					Data.compile = true;
					startActivity(new Intent(FileBrowser.this, RunActivity.class).setAction(Intent.ACTION_VIEW).setData(Uri.fromFile(new File(Data.fn))));
					dl.dismiss();
					finish();
				}
			}
		);
		noBtn.setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View v)
				{
					fill(new File(dir));
					dl.dismiss();
				}
			}
		);
		dl.show();
		return true;
	}

	public void decompile(String fn)
	{
		Data.decompile = true;
		Data.fn = fn;
		Data.dir = currentDir.getAbsolutePath();
		startActivity(new Intent(this, RunActivity.class).setAction(Intent.ACTION_VIEW).setData(Uri.fromFile(new File(fn))));
		finish();
	}

	public void home()
	{
		if (Data.flag == false)
		{
			currentDir = new File(Data.data);
		}
		else
		{
			currentDir = new File(Data.path);
		}
		Data.flag = !Data.flag;
		reset();
	}

	public void help()
	{
		final Dialog dl=new Dialog(this);
		dl.setTitle(getString(R.string.select_lang));
		dl.setContentView(R.layout.lang);
		LinearLayout ll=(LinearLayout)dl.findViewById(R.id.ll);
		ll.setOrientation(1);
		for(Language lg:Language.values()){
			Button b=new Button(this);
			b.setText(lg.toString());
			ll.addView(b);
			b.setOnClickListener(new View.OnClickListener()
				{
					public void onClick(View v)
					{
						IO.mkdirs(Data.data+"/tmp");
						IO.write(Data.data+"/tmp/lang",((Button)v).getText().toString());
						dl.dismiss();
						reset();
					}
				}
			);
		}
		dl.show();
	}

	public void newfile()
	{
		final Dialog dl=new Dialog(this);
		dl.setContentView(R.layout.newfile);
		dl.setTitle(getString(R.string.enter_name));
		et = (EditText)dl.findViewById(R.id.et);
		et.setBackgroundColor(Color.rgb(255, 255, 255));
		et.setTextColor(Color.rgb(0, 0, 0));
		Button yesBtn=(Button)dl.findViewById(R.id.file);
		Button noBtn=(Button)dl.findViewById(R.id.folder);
		yesBtn.setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View v)
				{
					if (et.getText().length() > 0)
					{
						File f=new File(currentDir + "/" + et.getText().toString());
						Data.fn = f.toString();
						IO.write(Data.fn, newfile(Data.fn));
						if (Data.fn.endsWith(".c") || Data.fn.endsWith(".cpp"))
						{
							IO.delete(Data.data + "/temp");
						}
					}
					dl.dismiss();
					reset();
				}
			}
		);
		noBtn.setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View v)
				{
					if (et.getText().length() > 0)
					{
						File f=new File(currentDir + "/" + et.getText().toString());
						f.mkdirs();
						Data.dir = f.getParent();
					}
					dl.dismiss();
					reset();
				}
			}
		);
		dl.show();
	}

	public static String newfile(String fn)
	{
		String pack="";
		File f=new File(fn);
		if (fn.endsWith(".java"))
		{
			String n=strip(f.getName());
			String txt =
				"public class " + n + " {\n" +
				"public static void main(String[] args){\n" +
				"System.out.println(\"" + n + "\");\n" +
				"}\n" +
				"}\n";
			if (f.toString().contains("/src"))
			{
				if (!f.getParent().endsWith("/src"))
				{
					String p="";
					while (!f.getParent().endsWith("/src"))
					{
						p = f.getParent().substring(f.getParent().lastIndexOf("/") + 1) + "." + p;
						f = new File(f.getParent());
					}
					p = p.substring(0, p.length() - 1);
					pack = "package " + p + ";\n";
				}
			}
			pack = pack + txt;
		}
		else if (fn.endsWith(".c"))
		{
			pack = 
				"#include <stdio.h>\n" +
				"\n" +
				"int main(int argc, char **argv){\n" +
				"printf(\"5\");\n" +
				"return 0;\n" +
				"}\n";
		}
		else if (fn.endsWith(".cpp"))
		{
			pack = 
				"#include <iostream>\n" +
				"using namespace std;\n" +
				"int main(int argc, char **argv)\n" +
				"{\n" +
				"cout << \"5\" << endl;\n" +
				"return 0;\n" +
				"}\n";
		}
		else if (fn.endsWith(".py"))
		{
			pack = 
				"def a(x):\n" +
				"    print(range(x))\n" +
				"a(10)\n";
		}
		return pack;
	}

	public static String strip(String x)
	{
		return x.substring(0, x.indexOf("."));
	}

	public void reset()
	{
		Data.dir = currentDir.getAbsolutePath();
		startActivity(new Intent(this, FileBrowser.class).setAction(Intent.ACTION_VIEW).setData(Uri.fromFile(new File(Data.fn))));
		finish();
	}

	public boolean delete(final String fn)
	{
		final Dialog dl=new Dialog(this);
		dl.setContentView(R.layout.delete);
		dl.setTitle(getString(R.string.delete) + " " + new File(fn).getName() + " ?");
		Button yesBtn=(Button)dl.findViewById(R.id.yes);
		Button noBtn=(Button)dl.findViewById(R.id.no);
		yesBtn.setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View v)
				{
					if (new File(fn).isDirectory())
					{
						IO.delete(fn);
						String name=new File(fn).getName();
						toast(getString(R.string.folder) + " " + name + " " + getString(R.string.del_folder_ok));
					}
					else
					{
						IO.delete(fn);
						toast(getString(R.string.file) + " " + new File(fn).getName() + " " + getString(R.string.del_file_ok));
					}
					Data.dir = new File(fn).getParent();
					dl.dismiss();
					reset();
				}
			}
		);
		noBtn.setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View v)
				{
					dl.dismiss();
				}
			}
		);
		dl.show();
		return true;
	}
	public void functions(int mode, final String fn)
	{
		final Dialog dl=new Dialog(this);
		dl.setTitle(new File(fn).getName());
		if (mode == 1)
		{
			dl.setContentView(R.layout.dir);
		}
		else if (mode == 2)
		{
			dl.setContentView(R.layout.decompile);
			Button decompile=(Button)dl.findViewById(R.id.decompile);
			decompile.setOnClickListener(new View.OnClickListener()
				{
					public void onClick(View v)
					{
						decompile(fn);
						dl.dismiss();
					}
				}
			);
		}
		Button create=(Button)dl.findViewById(R.id.create);
		Button pack=(Button)dl.findViewById(R.id.pack);
		Button copy=(Button)dl.findViewById(R.id.copy);
		Button paste=(Button)dl.findViewById(R.id.paste);
		Button rename=(Button)dl.findViewById(R.id.rename);
		Button delete=(Button)dl.findViewById(R.id.delete);
		Button exit=(Button)dl.findViewById(R.id.exit);
		create.setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View v)
				{
					newfile();
					dl.dismiss();
				}
			}
		);
		pack.setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View v)
				{
					pack(fn);
					dl.dismiss();
				}
			}
		);
		copy.setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View v)
				{
					Data.copy = fn;
					toast(Data.copy);
					dl.dismiss();
				}
			}
		);
		paste.setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View v)
				{
					if (!Data.copy.equals(null))
					{
						toast(Data.copy + " ==> " + Data.dir);
						IO.copy(Data.copy, Data.dir);
						Run.sh("chmod -R 0777 " + Data.dir);
						toast(getString(R.string.copy_ok));
					}
					dl.dismiss();
					reset();

				}
			}
		);
		rename.setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View v)
				{
					rename(new File(fn).getName());
					dl.dismiss();
				}
			}
		);
		delete.setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View v)
				{
					delete(fn);
					dl.dismiss();
				}
			}
		);
		exit.setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View v)
				{
					dl.dismiss();
					finish();
					System.exit(0);
				}
			}
		);
		dl.show();
	}

	public boolean pack(String fn)
	{
		//Run.sh(CMD.path() + "cd " + Data.dir + " && 7za u " + new File(fn).getName() + ".zip " + fn);
		reset();
		return true;
	}

	public void rename(final String fn)
	{
		final Dialog dl=new Dialog(this);
		dl.setContentView(R.layout.rename);
		dl.setTitle(fn);
		et = (EditText)dl.findViewById(R.id.ret);
		et.setText(fn);
		Button rename=(Button)dl.findViewById(R.id.ren);
		rename.setText(R.string.rename);
		rename.setOnClickListener(new View.OnClickListener()
			{
				public void onClick(View v)
				{
					if (et.getText().length() > 0)
					{
						new File(Data.dir + "/" + fn).renameTo(new File(Data.dir + "/" + et.getText().toString()));

					}
					dl.dismiss();
					reset();
				}
			}
		);
		dl.show();
	}

	public String getSize(String fn)
	{
		File f=new File(fn);
		long modTime = f.lastModified();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long size = f.length();
		double fileSize;
		String strSize = null;
		java.text.DecimalFormat df = new java.text.DecimalFormat("#0.00");
		if (size >= 1073741824)
		{
			fileSize = (double)size / 1073741824.0;				
			strSize = df.format(fileSize) + " Gb";				
		}
		else if (size >= 1048576)
		{
			fileSize = (double)size / 1048576.0;				
			strSize = df.format(fileSize) + " Mb";				
		}
		else if (size >= 1024)
		{
			fileSize = (double)size / 1024;			
			strSize = df.format(fileSize) + " Kb";
		}
		else
		{
			strSize = Long.toString(size) + " B";
		}
		//((TextView) v.findViewById(R.id.mod)).setText(
		//dateFormat.format(new Date(modTime)));
		//if (f.isFile() && f.canRead())
		//	((TextView) v.findViewById(R.id.size)).setText(strSize);
		return strSize;
	}
}
