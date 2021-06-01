
namespace Windows_Shareboard_App
{
	partial class Form1
	{
		/// <summary>
		/// Required designer variable.
		/// </summary>
		private System.ComponentModel.IContainer components = null;

		/// <summary>
		/// Clean up any resources being used.
		/// </summary>
		/// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
		protected override void Dispose(bool disposing)
		{
			if (disposing && (components != null))
			{
				components.Dispose();
			}
			base.Dispose(disposing);
		}

		#region Windows Form Designer generated code

		/// <summary>
		/// Required method for Designer support - do not modify
		/// the contents of this method with the code editor.
		/// </summary>
		private void InitializeComponent()
		{
			this.label1 = new System.Windows.Forms.Label();
			this.clipview = new System.Windows.Forms.ListView();
			this.Text = ((System.Windows.Forms.ColumnHeader)(new System.Windows.Forms.ColumnHeader()));
			this.Time = ((System.Windows.Forms.ColumnHeader)(new System.Windows.Forms.ColumnHeader()));
			this.exitbtn = new System.Windows.Forms.Button();
			this.clearbtn = new System.Windows.Forms.Button();
			this.add = new System.Windows.Forms.Button();
			this.SuspendLayout();
			// 
			// label1
			// 
			this.label1.AutoSize = true;
			this.label1.Font = new System.Drawing.Font("Microsoft Sans Serif", 26.25F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
			this.label1.ForeColor = System.Drawing.Color.FromArgb(((int)(((byte)(64)))), ((int)(((byte)(64)))), ((int)(((byte)(64)))));
			this.label1.Location = new System.Drawing.Point(12, 9);
			this.label1.Name = "label1";
			this.label1.Size = new System.Drawing.Size(197, 39);
			this.label1.TabIndex = 0;
			this.label1.Text = "Shareboard";
			// 
			// clipview
			// 
			this.clipview.Columns.AddRange(new System.Windows.Forms.ColumnHeader[] {
            this.Text,
            this.Time});
			this.clipview.FullRowSelect = true;
			this.clipview.HideSelection = false;
			this.clipview.Location = new System.Drawing.Point(340, 12);
			this.clipview.Name = "clipview";
			this.clipview.Size = new System.Drawing.Size(448, 426);
			this.clipview.TabIndex = 1;
			this.clipview.UseCompatibleStateImageBehavior = false;
			this.clipview.View = System.Windows.Forms.View.Details;
			this.clipview.ItemActivate += new System.EventHandler(this.clipview_ItemActivate); 
			// 
			// Text
			// 
			this.Text.Text = "Clipboard Item";
			this.Text.Width = 350;
			// 
			// Time
			// 
			this.Time.Text = "Time";
			this.Time.Width = 100;
			// 
			// exitbtn
			// 
			this.exitbtn.Location = new System.Drawing.Point(19, 414);
			this.exitbtn.Name = "exitbtn";
			this.exitbtn.Size = new System.Drawing.Size(75, 23);
			this.exitbtn.TabIndex = 2;
			this.exitbtn.Text = "Exit";
			this.exitbtn.UseVisualStyleBackColor = true;
			this.exitbtn.Click += new System.EventHandler(this.exitbtn_Click);
			// 
			// clearbtn
			// 
			this.clearbtn.Location = new System.Drawing.Point(259, 414);
			this.clearbtn.Name = "clearbtn";
			this.clearbtn.Size = new System.Drawing.Size(75, 23);
			this.clearbtn.TabIndex = 3;
			this.clearbtn.Text = "Clear";
			this.clearbtn.UseVisualStyleBackColor = true;
			// 
			// add
			// 
			this.add.Location = new System.Drawing.Point(259, 385);
			this.add.Name = "add";
			this.add.Size = new System.Drawing.Size(75, 23);
			this.add.TabIndex = 4;
			this.add.Text = "Insert";
			this.add.UseVisualStyleBackColor = true;
			this.add.Click += new System.EventHandler(this.add_Click);
			// 
			// Form1
			// 
			this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
			this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
			this.ClientSize = new System.Drawing.Size(800, 450);
			this.Controls.Add(this.add);
			this.Controls.Add(this.clearbtn);
			this.Controls.Add(this.exitbtn);
			this.Controls.Add(this.clipview);
			this.Controls.Add(this.label1);
			this.Name = "Form1";
			this.ResumeLayout(false);
			this.PerformLayout();

		}

		#endregion

		private System.Windows.Forms.Label label1;
		private System.Windows.Forms.ListView clipview;
		private System.Windows.Forms.ColumnHeader Text;
		private System.Windows.Forms.ColumnHeader Time;
		private System.Windows.Forms.Button exitbtn;
		private System.Windows.Forms.Button clearbtn;
		private System.Windows.Forms.Button add;
	}
}

