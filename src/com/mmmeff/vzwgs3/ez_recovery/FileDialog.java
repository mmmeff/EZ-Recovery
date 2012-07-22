package com.mmmeff.vzwgs3.ez_recovery;

/*
 * Copyright (c) 2011 Sylvain DANGIN
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the University of California, Berkeley nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FileDialog
{
	// Strings
	private final String[][] LOCALES_STRING = {
			{"Select a file",
				"Select a directory",
				"Make a directory",
				"New file",
				"Create",
				"Cancel",
				"Enter a name",
				"Select",
			"Impossible to create directory"},
			{"Sélectionnez un fichier",
				"Sélectionnez un répertoire",
				"Créer un répertoire",
				"Nouveau fichier",
				"Créer",
				"Annuler",
				"Entrez un nom",
				"Sélectionner",
			"Impossible de créer le répertoire"}};

	private final int STRING_SELECT_FILE = 0;
	private final int STRING_SELECT_DIR = 1;
	private final int STRING_MAKE_DIR = 2;
	private final int STRING_NEW_FILE = 3;
	private final int STRING_CREATE = 4;
	private final int STRING_CANCEL = 5;
	private final int STRING_ENTER_NAME = 6;
	private final int STRING_SELECT = 7;
	private final int STRING_ERROR_MAKE_DIR = 8;

	private final int LANG_EN = 0;
	private final int LANG_FR = 1;

	private final int VIEW_GLOBAL_LAYOUT_ID = 1;
	private final int VIEW_LOCATION_TEXT_ID = 2;
	private final int VIEW_FILELIST_LIST_ID = 3;
	private final int VIEW_ACTION_LAYOUT_ID = 4;

	private final int MODE_SELECT_FILE = 0;
	private final int MODE_SELECT_DIR = 1;

	public static int ACTION_SELECTED_FILE = 0;
	public static int ACTION_SELECTED_DIRECTORY = 1;
	public static int ACTION_CANCEL = 2;

	// Configuration
	private final String DEFAULT_PATH = "/sdcard/";
	private final int LISTVIEW_ITEM_HEIGHT = 24;

	private int language = LANG_EN;
	private String currentPath = DEFAULT_PATH;
	private int mode = MODE_SELECT_FILE;
	private String suggestedFileName;

	private ArrayList<FileItem> currentFileList;
	private FileListAdapter fileListAdapter;
	private Context context;
	private LinearLayout globalLinearLayout;	
	private Dialog dialog;
	private ActionListener listener;

	public FileDialog(Context context)
	{
		this.context = context;
		globalLinearLayout = null;
		listener = null;

		dialog = new Dialog(context);
		currentFileList = getFileList(currentPath);
		initLanguage();

		suggestedFileName = LOCALES_STRING[language][STRING_ENTER_NAME];
	}

	// Choose the good language
	private void initLanguage()
	{
		if(context.getResources().getConfiguration().locale.getCountry().equals("FR"))
			language = LANG_FR;
	}

	// Set an action listener
	public void setListener(ActionListener listener)
	{
		this.listener = listener;
	}

	// Change default path
	public void setPath(String path)
	{
		currentPath = path;
	}

	public void setSuggestedFileName(String fileName)
	{
		suggestedFileName = fileName;
	}

	// Open dialog box for select file
	public void selectFile()
	{
		mode = MODE_SELECT_FILE;

		// Add make dir actions buttons
		final Button makeDirButton = getMakeDirButton();

		// Add new file actions buttons
		final Button newFileButton = new Button(context);
		newFileButton.setText(LOCALES_STRING[language][STRING_NEW_FILE]);
		newFileButton.setOnClickListener(new OnClickListener()
		{
			public void onClick(View arg0)
			{
				createFileDialog();
			}
		});

		showDialog(LOCALES_STRING[language][STRING_SELECT_FILE], makeDirButton, newFileButton);
	}

	// Open dialog box for select file
	public void selectFileStrict()
	{
		mode = MODE_SELECT_FILE;

		showDialog(LOCALES_STRING[language][STRING_SELECT_FILE], null, null);
	}

	// Open dialog for select directory
	public void selectDirectory()
	{
		mode = MODE_SELECT_DIR;

		// Add make dir actions buttons
		final Button makeDirButton = getMakeDirButton();

		// Add select actions buttons
		final Button selectButton = new Button(context);
		selectButton.setText(LOCALES_STRING[language][STRING_SELECT]);
		selectButton.setOnClickListener(new OnClickListener()
		{
			public void onClick(View arg0)
			{
				if (listener != null)
					listener.userAction(ACTION_SELECTED_DIRECTORY, currentPath);
				dialog.dismiss();
			}
		});

		showDialog(LOCALES_STRING[language][STRING_SELECT_DIR], makeDirButton, selectButton);
	}

	// Return button for making a directory
	private Button getMakeDirButton()
	{
		Button makeDirButton = new Button(context);
		makeDirButton.setText(LOCALES_STRING[language][STRING_MAKE_DIR]);
		makeDirButton.setOnClickListener(new OnClickListener()
		{
			public void onClick(View arg0)
			{
				createDirectoryDialog();
			}
		});
		return makeDirButton;
	}

	private void showDialog(String title, Button leftButton, Button rightButton)
	{
		// Global Linear Layout
		if (globalLinearLayout == null)
			globalLinearLayout = new LinearLayout(context);	
		else
			globalLinearLayout.removeAllViewsInLayout();
		final LinearLayout.LayoutParams globalLinearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
		globalLinearLayout.setOrientation(LinearLayout.VERTICAL);
		globalLinearLayout.setLayoutParams(globalLinearLayoutParams);
		globalLinearLayout.setId(VIEW_GLOBAL_LAYOUT_ID);

		// Text view for the location
		final TextView locationTextView = new TextView(context);
		locationTextView.setId(VIEW_LOCATION_TEXT_ID);
		locationTextView.setText(currentPath);
		globalLinearLayout.addView(locationTextView);

		// List view for file list
		final ListView fileListView = new ListView(context);
		final LinearLayout.LayoutParams listViewLayoutParams = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, 0);
		listViewLayoutParams.weight = 1.0f;
		fileListView.setLayoutParams(listViewLayoutParams);
		fileListView.setId(VIEW_FILELIST_LIST_ID);
		fileListAdapter = new FileListAdapter(context, currentFileList);
		fileListView.setAdapter(fileListAdapter);
		fileListView.setOnItemClickListener(new OnItemClickListener(){
			public void onItemClick(AdapterView<?> parent, View view, int pos, long id)
			{
				// When item of the list is clicked
				if (currentFileList.get(pos).isDirectory)
					changeDirectory(currentFileList.get(pos).name);
				else if (listener != null && mode == MODE_SELECT_FILE)
				{
					listener.userAction(ACTION_SELECTED_FILE, currentPath+currentFileList.get(pos).name);
					dialog.dismiss();
				}
			}});
		globalLinearLayout.addView(fileListView);

		if (rightButton != null && leftButton != null)
		{
			// Create a linear layout for actions
			LinearLayout actionLinearLayout = new LinearLayout(context);
			LinearLayout.LayoutParams actionLinearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			actionLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
			actionLinearLayout.setLayoutParams(actionLinearLayoutParams);
			actionLinearLayout.setId(VIEW_ACTION_LAYOUT_ID);
			globalLinearLayout.addView(actionLinearLayout);

			// Add buttons
			final LinearLayout.LayoutParams buttonLayoutParams = new LinearLayout.LayoutParams(0, LayoutParams.FILL_PARENT);
			buttonLayoutParams.weight = 0.5f;

			leftButton.setLayoutParams(buttonLayoutParams);
			rightButton.setLayoutParams(buttonLayoutParams);

			((LinearLayout)globalLinearLayout.findViewById(VIEW_ACTION_LAYOUT_ID)).addView(leftButton);
			((LinearLayout)globalLinearLayout.findViewById(VIEW_ACTION_LAYOUT_ID)).addView(rightButton);
		}

		dialog.setContentView(globalLinearLayout);
		// Cancel action
		dialog.setCancelable(true);
		dialog.setOnCancelListener(new OnCancelListener()
		{
			public void onCancel(DialogInterface arg0)
			{
				if (listener != null)
					listener.userAction(ACTION_CANCEL, null);
			}
		});
		dialog.setTitle(title);
		dialog.show();
	}

	private void createDirectoryDialog()
	{
		final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		final EditText dirName = new EditText(context);
		dirName.setText(LOCALES_STRING[language][STRING_ENTER_NAME]);
		dirName.selectAll();
		dialog.setView(dirName)
		.setPositiveButton(LOCALES_STRING[language][STRING_CREATE], new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface arg0, int arg1)
			{
				createDirectory(dirName.getText().toString());
			}
		})
		.setNegativeButton(LOCALES_STRING[language][STRING_CANCEL], null)
		.setTitle(LOCALES_STRING[language][STRING_MAKE_DIR]);
		dialog.show();
	}

	private void createFileDialog()
	{
		final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		final EditText fileName = new EditText(context);
		fileName.setText(suggestedFileName);
		fileName.selectAll();
		dialog.setView(fileName)
		.setPositiveButton(LOCALES_STRING[language][STRING_CREATE], new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface arg0, int arg1)
			{
				createDirectory(fileName.getText().toString());
			}
		})
		.setNegativeButton(LOCALES_STRING[language][STRING_CANCEL], null)
		.setTitle(LOCALES_STRING[language][STRING_NEW_FILE]);
		dialog.show();
	}

	private void createDirectory(String dirName)
	{
		File dir = new File(currentPath+dirName);
		try
		{
			if (!dir.mkdir())
				toastMsg(LOCALES_STRING[language][STRING_ERROR_MAKE_DIR]);
			else
				changeDirectory(dirName);
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private void changeDirectory(String fileName)
	{
		String nextPath = "";
		if (fileName.equals(".."))
			nextPath = getParentDirectory(currentPath);
		else
			nextPath = currentPath+fileName+'/';
		((TextView)globalLinearLayout.findViewById(VIEW_LOCATION_TEXT_ID)).setText(nextPath);
		currentPath = nextPath;
		currentFileList = getFileList(currentPath);
		fileListAdapter.updateData(currentFileList);
	}

	private String getParentDirectory(String path)
	{
		// Remove last '/'
		String parentPath = path.substring(0, path.length() - 1);
		int lastSlash = parentPath.lastIndexOf('/');
		// If parent directory is root
		if (lastSlash <= 0)
			return "/";
		else
			return parentPath.substring(0, lastSlash + 1);
	}

	private ArrayList<FileItem> getFileList(String path)
	{
		ArrayList<FileItem> fileList = new ArrayList<FileItem>();
		// Add ".." for parent directory if not root
		if (!path.equals("/"))
		{
			FileItem ret = new FileItem();
			ret.name = "..";
			ret.isDirectory = true;
			fileList.add(ret);
		}

		// List all files of path
		File directory = new File(path);
		for (File file : directory.listFiles())
		{
			FileItem item = new FileItem();
			item.name = file.getName();
			if (file.isDirectory())
				item.isDirectory = true;
			fileList.add(item);
		}
		Collections.sort(fileList, new FileItemComparator());
		return fileList;
	}

	public int dpiToPixels(int dpi)
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int)(dpi * scale + 0.5f);
	}

	private void toastMsg(String msg)
	{
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
	}

	public static interface ActionListener
	{
		void userAction(int action, String data);
	}

	private class FileItem
	{
		public String name;
		public boolean isDirectory = false;
	}

	private class FileItemComparator implements Comparator<FileItem>
	{
		public int compare(FileItem item1, FileItem item2)
		{
			FileItem file1 = (FileItem) item1;
			FileItem file2 = (FileItem) item2;
			int res = file1.name.compareToIgnoreCase(file2.name);
			if (file1.isDirectory != file2.isDirectory)
			{
				if (file1.isDirectory)
					return -1;
				else
					return 1;
			}
			return res;
		}
	}

	// Adapter for the files list view
	private class FileListAdapter extends BaseAdapter
	{
		ArrayList<FileItem> fileList;
		Context context;

		public FileListAdapter(Context context, ArrayList<FileItem> fileList)
		{
			this.context = context;
			this.fileList = fileList;
		}

		public int getCount()
		{
			return fileList.size();
		}

		public Object getItem(int pos)
		{
			return fileList.get(pos);
		}

		public long getItemId(int id)
		{
			return id;
		}

		public View getView(int pos, View convertView, ViewGroup parent)
		{
			TextView itemView = (TextView)convertView;
			if (itemView == null)
				itemView = new TextView(context);

			// Set name of the item
			itemView.setText(fileList.get(pos).name);
			// If the item is a directory, set text style to bold
			if (fileList.get(pos).isDirectory)
				itemView.setTypeface(null, Typeface.BOLD);
			else
				itemView.setTypeface(null, Typeface.NORMAL);
			// Change size
			itemView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, LISTVIEW_ITEM_HEIGHT);
			// Center vertically
			itemView.setGravity(Gravity.CENTER_VERTICAL);
			return itemView;
		}

		public void updateData(ArrayList<FileItem> newFileList)
		{
			fileList = newFileList;
			notifyDataSetChanged();
		}
	}    
}