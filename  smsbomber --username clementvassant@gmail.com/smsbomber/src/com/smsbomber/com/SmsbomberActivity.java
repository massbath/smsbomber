package com.smsbomber.com;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Contacts;
import android.provider.Contacts.People;
import android.telephony.gsm.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

@SuppressWarnings("deprecation")
public class SmsbomberActivity extends Activity implements OnClickListener {
    /** Called when the activity is first created. */
	private EditText text;
	private EditText nbSms;
	private Button envoyer;
	private AdView Pub;
	private String idAdMob;
	private ArrayList<String> Mescontacts;
	private AutoCompleteTextView autoComplete ;
	private EditText cDestinataire ;
	private String destinataire;
	private String numero;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.principal);
        
         text =  (EditText)findViewById(R.id.edText);
         envoyer = (Button)findViewById(R.id.btnEnvoyer);
         nbSms = (EditText)findViewById(R.id.edNbSms);
         nbSms.setText("1");
         cDestinataire = (EditText) findViewById(R.id.autoContact);
         envoyer.setOnClickListener(this);
        
         
         /** Partie concernant la publicité**/
         idAdMob = "a14ea562e277ca7";
         // Create the adView
         Pub = new AdView(this, AdSize.BANNER, idAdMob);
         LinearLayout layout = (LinearLayout)findViewById(R.id.Layoutpub);
      // Add the adView to it
         layout.addView(Pub);

         // Initiate a generic request to load it with an ad
         Pub.loadAd(new AdRequest());
        
         /** Partie concernant l'autocomplétion**/

        ListContact();
       //android.R.layout.simple_dropdown_item_1line permet de définir le style d'affichage de la liste
          autoComplete = (AutoCompleteTextView)findViewById(R.id.autoContact);
         ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line, Mescontacts);
         
       //On affecte cette liste d'autocompletion à notre objet d'autocompletion
 		autoComplete.setAdapter(adapter);
    }

    
    private void ListContact() 
    {
    	// notre tableau de contact
    	Mescontacts = new ArrayList<String>();
    	
        // instance qui permet de récupérer les contacts du téléphone avec une URI
    	ContentResolver ConnectApp = this.getContentResolver();
    	Uri uri = Contacts.People.CONTENT_URI;
    	
    	//structure que l'on souhaite avoir pour le stockage des contacts
         String[] projection = new String[] {People.NAME, People.NUMBER, People._ID };
         
        // on récupere les contacts dans un curseur
         Cursor cur = ConnectApp.query(uri, projection, null, null, null);
         this.startManagingCursor(cur);
 
         //on parcour le curseur pour stocker les contacts dans l'arraylist
         if (cur.moveToFirst()) {
             do {
                 String name = cur.getString(cur.getColumnIndex(People.NAME));
                 String num = cur.getString(cur.getColumnIndex(People.NUMBER));
                 String id = cur.getString(cur.getColumnIndex(Contacts.People._ID));
                 //on rajoute à l'arraylist le contact
                 Mescontacts.add(name+"<"+num+">");
             } while (cur.moveToNext());
         }
     }
 
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Pub.destroy();
	    super.onDestroy();
		
	}


	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if(arg0==envoyer)
		{
			if(text.getText().length()==0 ||nbSms.getText().length()==0 ||Integer.valueOf(nbSms.getText().toString()) == 0)
			{
				Toast.makeText(this, "Tous les champs ne sont pas corrects", Toast.LENGTH_SHORT).show();
			}
			else
			{	
				//on récupère le numéro de téléphone du contact en se servant des chevrons
				destinataire = cDestinataire.getText().toString();
				if(destinataire.contains("<") && destinataire.contains(">"))
				{
					int debut = destinataire.lastIndexOf("<");
					int fin = destinataire.lastIndexOf(">");
					
					if((debut!=-1) && (fin!=-1) && ((fin-debut)> 0))
					{
					numero =  destinataire.substring(debut+1,fin);
					}
					if(numero.contains("null"))
						{
						Toast.makeText(this, "Erreur dans le numéro saisie", Toast.LENGTH_SHORT).show();
						return;
						}
				}
				else try
						{
						int numsms = Integer.parseInt(destinataire);
						numero = String.valueOf(numsms);
						}
					catch(NumberFormatException e)
						{
						Toast.makeText(this, "Erreur dans le numéro saisie", Toast.LENGTH_SHORT).show();
						return;
						}
				
				String msg = text.getText().toString();
		
				
				
				for(int i = 1; i <= Integer.valueOf(nbSms.getText().toString()); i++)
				{
					SmsManager.getDefault().sendTextMessage(numero, null, msg, null, null);
					
				}
				
				Toast.makeText(this, "Messages envoyés",Toast.LENGTH_SHORT).show();
	                
			/**Réinitialisation des editTexts **/	
			text.setText("");
			cDestinataire.setText("");
			nbSms.setText("1");
			
			}	
			
		}
	}
 }