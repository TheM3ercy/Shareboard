using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Runtime.InteropServices;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.Text.Json.Serialization;
using System.Net.Http.Formatting;
using System.Text.Json;

namespace Windows_Shareboard_App
{
	public partial class Form1 : Form
	{
		public static string userkey = "";
		private static bool running = true;
		public delegate void pull_from_server();
		public pull_from_server pull_From_Server;
		Thread t;
		Thread t2;
		public delegate void set_clipboard();
		public set_clipboard set_Clipboard;
		private string last_clipboard ="";
		private List<Clipboard_Item> clipitems;
		public class Clipboard_Item
		{
			[JsonPropertyName("clipboard")]
			public string Text { get; set; }
			[JsonPropertyName("upload_date")]
			public string Date { get; set; }
			[JsonPropertyName("id")]
			public string id { get; set; }

			public override bool Equals(object obj)
			{
				return obj is Clipboard_Item item &&
					   id == item.id;
			}
		}
		
		private void pull_from_server_Method()
		{
			

			const string URL = "http://omnic-systems.com/shareboard/pull_request/";
			HttpClient client = new HttpClient();
			client.BaseAddress = new Uri(URL);
			client.DefaultRequestHeaders.Accept.Add(new MediaTypeWithQualityHeaderValue("application/json"));

			HttpResponseMessage response = client.GetAsync("?user_string=" + userkey).Result;
			if (response.IsSuccessStatusCode)
			{
				var data = response.Content.ReadAsStringAsync().Result;
				var array = JsonSerializer.Deserialize<IEnumerable<Clipboard_Item>>(data);
				foreach (Clipboard_Item clipboardItem in array)
				{
					if (!clipitems.Contains(clipboardItem))
					{
						var item = new ListViewItem(clipboardItem.Text);
						item.SubItems.Add(clipboardItem.Date);
						clipview.Items.Add(item);
						clipitems.Add(clipboardItem);
					}
				}
			}
			else
			{
				Console.WriteLine("{0} ({1})", (int)response.StatusCode, response.ReasonPhrase);
			}
			client.Dispose();
		}
		public Form1()
		{
			InitializeComponent();
			var f2 = new Login();
			f2.ShowDialog();
			pull_From_Server = new pull_from_server(pull_from_server_Method);
			t = new Thread(new ThreadStart(start_bg_sync));
			t.Start();
			set_Clipboard = new set_clipboard(set_clipboard_Method);
			t2 = new Thread(new ThreadStart(start_bg_clip));
			t2.Start();
			clipitems = new List<Clipboard_Item>();
		}
		#region Threads
		private void start_bg_sync()
		{
			bg_sync bg_thread = new bg_sync(this);
			bg_thread.Run();
		}
		private void start_bg_clip()
		{
			bg_clip bg_Clip = new bg_clip(this);
			bg_Clip.Run();
		}
		private void clip_sync()
		{
			clip bg = new clip(this);
			bg.Run();
		}
		#endregion
		private void exitbtn_Click(object sender, EventArgs e)
		{
			running = false;
			Application.Exit();
		}

		private void add_Click(object sender, EventArgs e)
		{

			const string URL = "http://omnic-systems.com/shareboard/get_request/";
			HttpClient client = new HttpClient();
			client.BaseAddress = new Uri(URL);
			client.DefaultRequestHeaders.Accept.Add(new MediaTypeWithQualityHeaderValue("application/json"));
			var response = client.GetAsync("?user_string=" + userkey + "&content=" + Clipboard.GetText());
			last_clipboard = Clipboard.GetText();
			pull_from_server_Method();


		}

		private void set_clipboard_Method()
		{
			if (!last_clipboard.Equals(Clipboard.GetText())){
				const string URL = "http://omnic-systems.com/shareboard/get_request/";
				HttpClient client = new HttpClient();
				client.BaseAddress = new Uri(URL);
				client.DefaultRequestHeaders.Accept.Add(new MediaTypeWithQualityHeaderValue("application/json"));
				var response = client.GetAsync("?user_string=" + userkey + "&content=" + Clipboard.GetText());
				last_clipboard = Clipboard.GetText();
				pull_from_server_Method();

			}
			if(clipview.SelectedItems.Count!=0)
			if (!clipitems[clipview.SelectedIndices[0]].Text.Equals("")&&!clipitems[clipview.SelectedIndices[0]].Text.Equals(last_clipboard)) {
					try
					{
						last_clipboard = Clipboard.GetText();
						Clipboard.SetText(clipitems[clipview.SelectedIndices[0]].Text);
						
					}
					catch (Exception) { 
					}
				}
				
		}

		private void clipview_ItemActivate(object sender, EventArgs e)
		{
			Thread thread = new Thread(new ThreadStart(clip_sync));
			thread.Start();
		}

		private void clearbtn_Click(object sender, EventArgs e)
		{
			List<ListViewItem> tempviewitems = new List<ListViewItem>();
			List<Clipboard_Item> tempclipitems = new List<Clipboard_Item>();
			const string URL = "http://omnic-systems.com/shareboard/delete/";
			HttpClient client = new HttpClient();
			client.BaseAddress = new Uri(URL);
			client.DefaultRequestHeaders.Accept.Add(new MediaTypeWithQualityHeaderValue("application/json"));
			foreach (int index in clipview.SelectedIndices)
			{
				var item = clipitems[index];
				HttpResponseMessage response = client.GetAsync("?id="+item.id+"&user_string="+userkey).Result;
				if (response.IsSuccessStatusCode)
				{
					var data = response.Content.ReadAsStringAsync().Result;
					tempviewitems.Add(clipview.Items[index]);
					tempclipitems.Add(clipitems[index]);
				}
			}
			foreach(ListViewItem i in tempviewitems)
			{
				clipview.Items.Remove(i);
			}
			foreach(Clipboard_Item i in tempclipitems)
			{
				clipitems.Remove(i);
			}
			client.Dispose();
		}
			

		private void button1_Click(object sender, EventArgs e)
		{

			pull_from_server_Method();

		}
		#region ThreadInvokes
		public class bg_clip
		{
			Form1 control;
			public bg_clip(Form1 form)
			{
				control = form;
			}
			public void Run()
			{
				while (running)
				{
					control.Invoke(control.set_Clipboard);
					Thread.Sleep(250);
				}

			}
		}

		public class clip
		{
			Form1 control;
			public clip(Form1 form)
			{
				control = form;
			}
			public void Run()
			{
					control.Invoke(control.set_Clipboard);
			}
		}
		//URL: https://docs.microsoft.com/en-us/dotnet/api/system.windows.forms.control.invoke?view=net-5.0 
		public class bg_sync
		{
			Form1 myFormControl1;
			public bg_sync(Form1 myForm)
			{
				myFormControl1 = myForm;
			}

			public void Run()
			{
				Thread.Sleep(100);
				while (running)
				{
					myFormControl1.Invoke(myFormControl1.pull_From_Server);
					Thread.Sleep(3000);
				}
			}
		}
		#endregion

	}
}
