using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Text;
using System.Text.Json;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace Windows_Shareboard_App
{
	public partial class Form2 : Form
	{
		class userkey{
			
			public string user_string { get;set; }
		}

		public Form2()
		{
			InitializeComponent();
			textBox1.Select();
		}

		private void button1_Click(object sender, EventArgs e)
		{
			Application.Exit();
		}

		private void button2_Click(object sender, EventArgs e)
		{
			label3.Visible = false;
			label4.Visible = false;
			if(!textBox1.Text.Equals("")||!textBox2.Text.Equals(""))
			{
				const string URL = "http://omnic-systems.com/shareboard/pull_key/";
				HttpClient client = new HttpClient();
				client.BaseAddress = new Uri(URL);
				client.DefaultRequestHeaders.Accept.Add(new MediaTypeWithQualityHeaderValue("application/json"));

				HttpResponseMessage response = client.GetAsync("?username="+textBox1.Text+"&password="+textBox2.Text).Result;
				if (response.IsSuccessStatusCode)
				{
					var data = response.Content.ReadAsStringAsync().Result;
					if (data.Equals("Username or Password incorrect!"))
					{
						label4.Visible = true;
					}
					else
					{
						var array = JsonSerializer.Deserialize<IEnumerable<userkey>>(data);

						foreach (var user in array)
						{
							Form1.userkey = user.user_string;
						}
						this.DialogResult = DialogResult.OK;
					}
				}
				else
				{
					Console.WriteLine("{0} ({1})", (int)response.StatusCode, response.ReasonPhrase);
				}
				client.Dispose();
			}
			else
			{
				label3.Visible=true;
			}

		}

		private void textBox2_KeyDown(object sender, KeyEventArgs e)
		{
			if (e.KeyValue == (char)Keys.Enter)
				button2_Click(sender, e);


		}
	}
}
