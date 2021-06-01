using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Runtime.InteropServices;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace Windows_Shareboard_App
{
	public partial class Form1 : Form
	{
		public Form1()
		{
			InitializeComponent();
		}


		private void exitbtn_Click(object sender, EventArgs e)
		{
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
			Clipboard.SetText(clipview.SelectedItems[0].Text);
		}
	}
}
