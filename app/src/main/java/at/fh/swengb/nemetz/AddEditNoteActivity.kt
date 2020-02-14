package at.fh.swengb.nemetz

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add_edit_note.*

class AddEditNoteActivity : AppCompatActivity() {

    companion object {
        val EXTRA_ADDED_OR_EDITED_RESULT = "ADD_OR_EDITED_RESULT"
        val USER_TOKEN = "TOKEN"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_note)

        val extra: String? = intent.getStringExtra(NoteListActivity.NOTEID)

        if(extra != null){
            val note:Note? = NoteRepository.getNoteById(this, extra)
            if(note != null) {
                addEditNote_title.setText(note.title)
                addEditNote_content.setText(note.text)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.note_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item?.itemId) {
            R.id.savenote -> {

                val extra: String? = intent.getStringExtra(NoteListActivity.NOTEID)
                val sharedPreferences = getSharedPreferences(packageName, Context.MODE_PRIVATE)
                val token = sharedPreferences.getString(USER_TOKEN, null)

                if (
                    (extra != null) &&
                    (addEditNote_content.text.toString().isNotEmpty() || addEditNote_title.text.toString().isNotEmpty()) &&
                    (token != null))
                {
                    val note = Note(extra, addEditNote_title.text.toString(), addEditNote_content.text.toString(), true)
                    NoteRepository.addNote(this, note)
                    NoteRepository.uploadNote(
                        token,
                        note,
                        success = {
                        NoteRepository.addNote(this, it)
                    },
                        error = {
                        Log.e("Upload", it)
                    })

                    val resultIntent = intent
                    resultIntent.putExtra(EXTRA_ADDED_OR_EDITED_RESULT, "ADDED")
                    Log.e("ADD_NOTE", "Added note")
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                }
                else {
                    Toast.makeText(this, this.getString(R.string.fill_message) , Toast.LENGTH_SHORT).show()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
