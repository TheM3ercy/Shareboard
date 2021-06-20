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

		public class Clipboard_Item
		{
			[JsonPropertyName("clipboard")]
			public string Text { get; set; }
			[JsonPropertyName("upload_date")]
			public string Date { get; set; }

		}

		private void pull_from_server_Method()
		{
			clipview.Items.Clear();
			const string URL = "http://omnic-systems.com/shareboard/pull_request/";
			HttpClient client = new HttpClient();
			client.BaseAddress = new Uri(URL);
			client.DefaultRequestHeaders.Accept.Add(new MediaTypeWithQualityHeaderValue("application/json"));

			HttpResponseMessage response = client.GetAsync("?user_string=" + userkey).Result;
			if (response.IsSuccessStatusCode)
			{
				var data = response.Content.ReadAsStringAsync().Result;
				var array = JsonSerializer.Deserialize<IEnumerable<Clipboard_Item>>(data);
				foreach (var clipboardItem in array)
				{
					var item = new ListViewItem(clipboardItem.Text);
					item.SubItems.Add(clipboardItem.Date);
					clipview.Items.Add(item);
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
			var f2 = new Form2();
			f2.ShowDialog();
			pull_From_Server = new pull_from_server(pull_from_server_Method);
			t = new Thread(new ThreadStart(ThreadFunction));
			t.Start();
		}
		private void ThreadFunction()
		{
			bg_sync myThreadClassObject = new bg_sync(this);
			myThreadClassObject.Run();
		}

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

			pull_from_server_Method();


		}



		private void clipview_ItemActivate(object sender, EventArgs e)
		{
			if (!clipview.SelectedItems[0].Text.Equals(""))
				Clipboard.SetText(clipview.SelectedItems[0].Text);
		}

		private void clearbtn_Click(object sender, EventArgs e)
		{
			clipview.Items.Clear();
		}

		private void button1_Click(object sender, EventArgs e)
		{

			pull_from_server_Method();

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
				while (running)
				{
					myFormControl1.Invoke(myFormControl1.pull_From_Server);
					Thread.Sleep(3000);
				}
			}
		}

	}
}
