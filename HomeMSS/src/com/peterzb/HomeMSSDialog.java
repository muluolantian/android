package com.peterzb;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.DisplayMetrics;
import android.app.ProgressDialog;

import com.peterzb.user.*;

public class HomeMSSDialog extends Activity 
{
	//声明用户登录验证线程结束通知给UI消息
	protected static final int ACTIONCALLBACK = 0x1000;
	//private TextView loginTitle;
	private Button btnLogin;
	private EditText txtUserName;
	private EditText txtPwd;

	private int resultCode;
	private String userName;
	private String pwd;
	
	public ProgressDialog myDialog = null;	
	public Handler mHandler;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
//        loginTitle = (TextView)this.findViewById(R.id.login_title);
//        DisplayMetrics dm = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(dm);
//        int left = (dm.widthPixels - loginTitle.getWidth())/2;
//        left -= loginTitle.getPaddingLeft();
//        loginTitle.setPadding(left, 25, 0, 15);
        
        txtUserName = (EditText)this.findViewById(R.id.txtUserName);
        txtPwd = (EditText)this.findViewById(R.id.txtPwd);
        
        //通过Handler来接收进程所传递的信息并提示给用户
        mHandler = new Handler()
        {
        	public void handleMessage(Message msg)
        	{
        		switch (msg.what)
        		{
					case HomeMSSDialog.ACTIONCALLBACK:
                		if(resultCode != 0)
                		{
                			int []prompt = new int[4];
                			prompt[0] = R.string.LoginPrompt1;
                			prompt[1] = R.string.LoginPrompt2;
                			prompt[2] = R.string.LoginPrompt3;
                			prompt[3] = R.string.LoginPrompt4;
                			
                			Alert(R.string.LoginPrompt, prompt[resultCode - 1]);
                			btnLogin.setEnabled(true);
                		}
						break;
				}
        		super.handleMessage(msg);
        	}
        };
        
        btnLogin = (Button)this.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) 
            {
            	userName = txtUserName.getText().toString();
                if(userName.trim().length() == 0)
                {
                	Toast.makeText(getApplicationContext(), R.string.RequireUserName, 
                			Toast.LENGTH_LONG)
                	.show();
                }
                else
                {
                	pwd = txtPwd.getText().toString();
                	if(pwd.trim().length() == 0)
                	{
                		Alert(R.string.LoginPrompt, R.string.RequirePwd);
                	}
                	else
                	{
                		btnLogin.setEnabled(false);
                		final CharSequence strDialogTitle = 
                			getString(R.string.prompt);
                		final CharSequence strDialogBody = 
                			getString(R.string.checkuser);
                		
                		// display progress dialog
                		myDialog = ProgressDialog.show
                		(
                				HomeMSSDialog.this, 
                				strDialogTitle, 
                				strDialogBody,
                				true
                		);
                		new Thread()
                		{
                			public void run()
                			{
                				try
                				{
                					UserDAO dao = new UserDAO();
                            		String url = getString(R.string.CheckUserAction);
                            		resultCode = dao.CheckUser(url, userName, pwd);
                				}
                				catch (Exception e) 
                				{
									e.printStackTrace();
								}
                				finally
                				{
                					myDialog.dismiss();
                            		
                            		Message m = new Message();
                            		m.what = HomeMSSDialog.ACTIONCALLBACK;
                            		HomeMSSDialog.this.mHandler.sendMessage(m);               					
                				}
                			}
                		}.start();
                	}
                }
            }
        });
        
        Button btnCancel = (Button)this.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new Button.OnClickListener(){
        	@Override
            public void onClick(View v) 
            {
        		finish();
            }
        });
    }
    
    private void Alert(int title, int message)
    {
    	new AlertDialog.Builder(HomeMSSDialog.this)
    	.setTitle(title)
    	.setMessage(message)
    	.setPositiveButton
    	(
    		R.string.str_ok,
    		new DialogInterface.OnClickListener() 
    		{						
				@Override
				public void onClick(DialogInterface dialog, int which) 
				{
				}
			}
    	)
    	.show();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		menu.add(0, 0, 0, R.string.app_about);
		menu.add(0, 1, 1, R.string.str_exit);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) 
		{
			case 0:
				openAboutDialog();
				break;	        
			case 1:
				openOptionsDialog();
				break;	 
			default:
				break;
		}
		return true;
	}
	
	// 弹出关于对话框
	private void openAboutDialog()
	{
		LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_layout, 
        		(ViewGroup) findViewById(R.id.toast_layout_root));
        ImageView image = (ImageView) layout.findViewById(R.id.image);
        image.setImageResource(R.drawable.logo);
        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(R.string.app_about_msg);
        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
	}
	
	/**
	 * 弹出退出程序提示对话框
	 */
	private void openOptionsDialog()
	{
		new AlertDialog.Builder(this)
		.setTitle(R.string.app_exit)
		.setMessage(R.string.app_exit_msg)
		.setNegativeButton(R.string.str_no, new DialogInterface.OnClickListener()
		{		
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
			}
		})
		.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener()
		{		
			@Override
			public void onClick(DialogInterface dialog, int which) 
			{
				finish();
			}
		})
		.show();
	}
}