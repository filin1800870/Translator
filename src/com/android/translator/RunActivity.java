package com.android.translator;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import com.android.fm.*;
import com.android.utils.*;
import java.io.*;
import java.util.*;

public class RunActivity extends Activity
{
	public EditText ed;
	ScrollView sv;
	public int lines;
	long x,y;
	Run run;
	public String cmd;

    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		ed = (EditText)findViewById(R.id.ed);
		sv = (ScrollView)findViewById(R.id.sv);
		ed.setTypeface(Typeface.DEFAULT_BOLD, Typeface.BOLD);
		ed.setTextColor(Color.rgb(150, 0, 0));
		ed.setTextSize(20);
		run();
    }

	public void install(String apk)
	{
		startActivity(new Intent().setAction(Intent.ACTION_VIEW).setDataAndType(Uri.fromFile(new File(apk)), "application/vnd.android.package-archive"));
	}

	public void run()
	{
		cls();
		IO.write(Data.script, cmd);
		run = new Run(this);
		run.start();
	}

	public Handler handler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			String result=(String)msg.obj;
			if (result.startsWith("install"))
			{
				install((result.split(" ")[1]).split("\n")[0]);
			}
			else
			{
				ed.append(result);
			}
			if (lines >= 300)
			{
				lines = 0;
				ed.setText("");
			}
			scrollDown();
			lines++;
		}
	};

	public void scrollDown()
	{
		final ScrollView esv = (ScrollView)findViewById(R.id.sv);
		esv.post(new Runnable()
			{
				public void run()
				{
					esv.fullScroll(ScrollView.FOCUS_DOWN);
				}
			}
		);
		//esv.setFocusable(false);
	}

	@Override
	public void onBackPressed()
	{
		y = new Date().getTime();
		if ((y - x) <= 500)
		{
			finish();
		}
		x = y;
		if (Data.run == true)
		{
			prompt();
		}
		else
		{
			finish();
		}
	}

	public void prompt()
	{
		String[] ss=ed.getText().toString().split("\n");
		final String s=ss[ss.length - 1];
		if (!s.isEmpty())
		{
			ed.append("\n");
		}
		if (s.equals("exit"))
		{
			Data.run = false;
			finish();
		}
		else if (s.equals("cls"))
		{
			cls();
		}
	}

	public void cls()
	{
		lines = 0;
		ed.setText("");
	}

	public void reset()
	{
		startActivity(new Intent(this, RunActivity.class).setAction(Intent.ACTION_VIEW).setData(Uri.fromFile(new File(Data.fn))));
		finish();
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
			reset();
			return true;
		}
		if (id == R.id.exit)
		{
			finish();
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
