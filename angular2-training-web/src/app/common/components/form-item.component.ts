import {Component, Input, OnInit} from '@angular/core';
import { FormGroup } from '@angular/forms';
import {FormMetadata} from "../templates/form-metadata";

@Component({
  selector: 'form-item',
  templateUrl: './form-item.html',
  directives: []
})
export class FormItemComponent implements OnInit {

  @Input() data: FormMetadata<any>;

  @Input() form: FormGroup;

  constructor() {

  }

  ngOnInit() {
    console.log(this.data, this.form);
  }

  get isValid() {
    return this.form.controls[this.data.metadata.key].valid;
  }

}
