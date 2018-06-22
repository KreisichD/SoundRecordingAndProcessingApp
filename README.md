# SoundRecordingAndProcessingApp
 Android Sound Recording app

App is a demo, it has got it's functionalities, but the UI is not any kind of user-friendly.

Android app, should work properly on Android API 24 (7.0 Nougat)  

  
Application provides recording possibilities, with cutting silent blocks of a recording.  
App uses low level Android API (AudioRecorder) for recording sound, files along with metadata are saved in external phone memory.  

App contains three activites:  

MainActivity - Data is loaded during onCreate, here we've got a choice what do we want to do in App.  
RecordingActivity - Allows to record new sounds.  
ListActivity - Contains list view with recorded elements, and an options menu, where you can find options like 
  Concatenate - concatenates two selected files into one first -> second.  
  Delete - deletes recording from external memory and deletes metadata.  
  Play - gives possibility to listen to the recording.  
  Stop   
  Pause/Unpause  

Recording process is done using two threads, which starts when record button is clicked.  
Thread 1 - Record - This thread saves sound data blocks into a concurrent queue  
Thread 2 - Process - This thread processes the queue, by deleting silent blocks, and writing consecutive byte blocks with audio to a  temporal file.



