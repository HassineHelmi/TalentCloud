import { Component, OnInit } from '@angular/core';
import { GalleryFacade } from '@mf-app/shared/data-store';
import { HttpClient } from '@angular/common/http';

declare var MediaRecorder: any;

@Component({
  selector: 'mf-app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent implements OnInit {
  constructor(private galleryFacade: GalleryFacade,private http: HttpClient) {}
  ngOnInit(): void {
    this.galleryFacade.init();
  }
  transcribedText: string = '';
  isRecording = false;
  mediaRecorder: any;
  audioChunks: BlobPart[] = [];

  startRecording() {
    navigator.mediaDevices.getUserMedia({ audio: true }).then(stream => {
      this.mediaRecorder = new MediaRecorder(stream);
      this.audioChunks = [];

      this.mediaRecorder.ondataavailable = (event: any) => {
        this.audioChunks.push(event.data);
      };

      this.mediaRecorder.onstop = () => {
        const audioBlob = new Blob(this.audioChunks, { type: 'audio/wav' });
        const formData = new FormData();
        formData.append('file', audioBlob);

        this.http.post<{ text: string }>('http://localhost:8081/api/transcribe', formData)
          .subscribe(response => {
            this.transcribedText = response.text;
          });
      };

      this.mediaRecorder.start();
      this.isRecording = true;
    });
  }

  stopRecording() {
    if (this.mediaRecorder && this.isRecording) {
      this.mediaRecorder.stop();
      this.isRecording = false;
    }
  }
}









