package com.android.translator;

import android.app.*;
import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import com.android.fm.*;
import com.android.utils.*;
import java.io.*;

public class MainActivity extends Activity 
{
	EditText ed;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		ed = (EditText)findViewById(R.id.ed);
		init();
		ed.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
		ed.setTextSize(20);
		Data.dir = new File(Data.fn).getParent();
		ed.setText(IO.read(Data.fn));
    }

	public void install(String apk)
	{
		startActivity(new Intent().setAction(Intent.ACTION_VIEW).setDataAndType(Uri.fromFile(new File(apk)), "application/vnd.android.package-archive"));
		finish();
		System.exit(0);
	}

	public void init()
	{
		Data.path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Translator";
		Data.data = getFilesDir().getParent();
		Data.def = Data.data + "/tmp/default";
		Data.apk = getPackageResourcePath();
		Data.script = Data.data + "/tmp/script.sh";
		Data.lang=IO.read(Data.data+"/tmp/lang");
		Intent i = this.getIntent();
		if (i.getAction().equals(Intent.ACTION_VIEW))
		{
			Data.fn = i.getData().getPath();
		}
		if(Data.lang.isEmpty()){
			Data.lang="ru";
			IO.write(Data.data+"/tmp/lang","ru");
		}
		if (Data.fn.isEmpty())
		{
			Data.fn = IO.read(Data.def);
			if (Data.fn.isEmpty())
			{
				IO.mkdirs(Data.path);
				Data.fn = Data.path + "/test.sh";
				IO.write(Data.def, Data.fn);
			}
		}
		if (Data.fn.endsWith(".apk"))
		{
			install(Data.fn);
		}
	}

	public Handler handler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			String result=(String)msg.obj;
			ed.append(result);
		}
	};

	public void run()
	{
		Data.program = ed.getText().toString();
		if (!Data.program.equals(IO.read(Data.fn)))
		{
			IO.write(Data.fn, Data.program);
			if (Data.fn.endsWith(".java"))
			{
				IO.delete(IO.project(Data.fn) + "/classes");
			}
			else if (Data.fn.endsWith(".c") || Data.fn.endsWith(".cpp"))
			{
				IO.delete(Data.data + "/temp");
			}
		}
		IO.write(Data.def, Data.fn);
		startActivity(new Intent(this, RunActivity.class));
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

	@Override
	public void onBackPressed()
	{
		run();
	}

	public void open()
	{
		startActivity(new Intent(this, FileBrowser.class).setAction(Intent.ACTION_VIEW).setData(Uri.fromFile(new File(Data.fn))));
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int id = item.getItemId();
		if (id == R.id.run)
		{
			run();
			return true;
		}
		if (id == R.id.exit)
		{
			finish();
			Run.sh("kill $#");
			System.exit(0);
			return true;
		}
		if (id == R.id.open)
		{
			open();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
