package com.android.fm;

import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.graphics.drawable.*;
import android.view.*;
import android.widget.*;
import com.android.translator.*;
import java.util.*;


public class FileArrayAdapter extends ArrayAdapter<Option>
{

	private Context c;
	private int id;
	private List<Option>items;

	public FileArrayAdapter(Activity context, int textViewResourceId, List<Option> objects)
	{
		super(context, textViewResourceId, objects);
		//this.context = context;
		c = context;
		id = textViewResourceId;
		items = objects;
	}

	public Option getItem(int i)
	{
		return items.get(i);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{

		View v = convertView;
		if (v == null)
		{
			LayoutInflater vi = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(id, null);
		}


		final Option o = items.get(position);

		if (o != null)
		{
			TextView t1 = (TextView) v.findViewById(R.id.filemanager_title);
			TextView t2 = (TextView) v.findViewById(R.id.filemanager_desc);
			ImageView i = (ImageView) v.findViewById(R.id.fileicon);

			if (t1 != null)
				t1.setText(o.getName());
			if (t2 != null)
			{
				t2.setText(o.getData());
				if (o.getData().equalsIgnoreCase("folder"))
				{
					i.setImageResource(R.drawable.filemanager_folder);
				}
				else if (o.getData().equalsIgnoreCase("parent directory"))
				{
					i.setImageResource(R.drawable.filemanager_parent);
				}
				else if (o.getName().endsWith(".png") || (o.getName().endsWith(".jpg")) || (o.getName().endsWith(".jpeg")))
				{
					i.setImageResource(R.drawable.filemanager_image);
				}
				else if (o.getName().endsWith(".apk"))
				{
					i.setImageDrawable(getBanner(o.getPath()));
				}
				else if (o.getName().endsWith(".jar"))
				{
					i.setImageResource(R.drawable.jar);
				}
				else if (o.getName().endsWith(".MF"))
				{
					i.setImageResource(R.drawable.newfile);
				}
				else if (o.getName().endsWith("AndroidManifest.xml"))
				{
					i.setImageResource(R.drawable.newfile);
				}
				else if (o.getName().endsWith(".txt") || (o.getName().endsWith(".text")) || (o.getName().endsWith(".xml")))
				{
					i.setImageResource(R.drawable.filemanager_text);
				}
				else if (o.getName().endsWith(".m4a") || (o.getName().endsWith(".mp3")) || (o.getName().endsWith(".wav"))
						 || (o.getName().endsWith(".ogg")))
				{
					i.setImageResource(R.drawable.filemanager_sound);
				}
				else if (o.getName().endsWith(".wmv") || (o.getName().endsWith(".avi")) || (o.getName().endsWith(".mkv"))
						 || (o.getName().endsWith(".mp4")))
				{
					i.setImageResource(R.drawable.filemanager_movie);
				}
				else if (o.getName().endsWith(".zip") || (o.getName().endsWith(".rar")) || (o.getName().endsWith(".7z"))
						 || (o.getName().endsWith(".gz")))
				{
					i.setImageResource(R.drawable.filemanager_zip);
				}
				else if (o.getName().endsWith(".pdf"))
				{
					i.setImageResource(R.drawable.filemanager_pdf);
				}
				else if (o.getName().endsWith(".db"))
				{
					i.setImageResource(R.drawable.filemanager_database);
				}
				else if (o.getName().endsWith(".nomedia"))
				{
					i.setImageResource(R.drawable.build_file64);
				} 
				else if (o.getName().endsWith(".java"))
				{
					i.setImageResource(R.drawable.a_java);
				}
				else if (o.getName().endsWith(".sh"))
				{
					i.setImageResource(R.drawable.a_shell);
				}
				else
				{
					i.setImageResource(R.drawable.filemanager_file);
				}
			}
		}
		return v;
	}

	public Drawable getBanner(String fn)
	{
		PackageManager pm = c.getPackageManager();
		PackageInfo info = pm.getPackageArchiveInfo(fn,
													PackageManager.GET_ACTIVITIES);
		if (info != null)
		{
			ApplicationInfo appInfo = info.applicationInfo;
			appInfo.sourceDir = fn;
			appInfo.publicSourceDir = fn;
			try
			{
				return appInfo.loadIcon(pm);
			}
			catch (OutOfMemoryError e)
			{
				//      Log.e("ApkIconLoader", e.toString());
			}
		}
		return null;
	}
}

