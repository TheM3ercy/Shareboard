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
		private string userkey = "?user_string=2560c90aaf8c3f8";
		private bool running = true;
		
		public class Clipboard_Item
		{
			[JsonPropertyName("clipboard")]
			public string Text { get; set; }
			[JsonPropertyName("upload_date")]
			public string Date { get; set; }

		}

		private void pull_from_server()
		{
			const string URL = "http://omnic-systems.com/shareboard/pull_request/";
			HttpClient client = new HttpClient();
			client.BaseAddress = new Uri(URL);
			client.DefaultRequestHeaders.Accept.Add(new MediaTypeWithQualityHeaderValue("application/json"));

			HttpResponseMessage response = client.GetAsync(userkey).Result;
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
				Console.WriteLine("{0} ({1})", (int) response.StatusCode, response.ReasonPhrase);
			}
			client.Dispose();
		}



		public Form1()
		{
			InitializeComponent();
			
		}


		private void exitbtn_Click(object sender, EventArgs e)
		{
			running = false;
			Application.Exit();
		}

		private void add_Click(object sender, EventArgs e)
		{
			var item = new ListViewItem(Clipboard.GetText());
			item.SubItems.Add(DateTime.UtcNow.ToString());
			clipview.Items.Add(item);
		}



		private void clipview_ItemActivate(object sender, EventArgs e)
		{
			if(!clipview.SelectedItems[0].Text.Equals(""))
			Clipboard.SetText(clipview.SelectedItems[0].Text);
		}

		private void clearbtn_Click(object sender, EventArgs e)
		{
			clipview.Items.Clear();
		}

		private void button1_Click(object sender, EventArgs e)
		{
			clipview.Items.Clear();
			pull_from_server();
		}
	}
}
