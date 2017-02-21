import {OnInit, OnDestroy} from "@angular/core";
import {Router, ActivatedRoute}       from '@angular/router';
import {BaseEntity} from "../entities/baseEntity";
import {EntityService, BaseEntityServiceImpl} from "../services/entity/base-entity.service";
import {FormControlService} from "../templates/form-control.service";
import { FormGroup }                 from '@angular/forms';
import {FormMetadata, DropdownListItem} from "../templates/form-metadata";
import {Observable}     from 'rxjs/Rx';
import {BaseDomainTemplate} from "../templates/baseDomain.template";

export abstract class BaseEntityComponent<E extends BaseEntity, S extends BaseEntityServiceImpl<E>, T extends BaseDomainTemplate<E>>
  implements OnInit, OnDestroy {

  model: E = undefined;

  form: FormGroup;

  formMetadata: FormMetadata<any>[];

  title: string;

  protected sub: any;

  constructor(protected entityService: S,
              protected formControlService: FormControlService,
              protected route: ActivatedRoute,
              protected router: Router,
              protected domainTemplate:T) {
  }

  /**
   * This is called when component is created
   * @returns {null}
   */
  ngOnInit() {
    console.log('ngOnInit() ->');
    this.title = this.getTitle();
    this.buildFormControlGroup(undefined);
    this.sub = this.route.params.subscribe(
      params => {
        if (params['id']) {
          let id: number = +params['id'];
          this.get(id).subscribe(entity => {
            console.debug('Test get()-> ', entity);
            // this.buildFormControlGroup(entity)
            this.model = entity;
          });
        }
      }
    );
  }

  protected getTitle(): string {
    return this.domainTemplate.getDetailTitle();
  }


  /**
   * build form ControlGroup for dynamic form
   * @param entity
   */
  protected buildFormControlGroup(entity: E) {
    console.debug('buildFormControlGroup ->', entity);
    this.formMetadata = this.mapDomainFormMetadata(entity);
    this.form = this.formControlService.getControlGroup(this.formMetadata);
  }

  protected setList<D>(key:string, dataSubscriber:Observable<D[]>, map:(D) => DropdownListItem) {
    this.formMetadata
      .filter(item => item.metadata.key === key)
      .map(item => {
        dataSubscriber.subscribe(data => item.list = data.map(item => {
          return map(item);
        }));
        return item;
      });
  }

  /**
   * map Domain object to form metadata array
   * @param entity
   */
  mapDomainFormMetadata(entity: E): FormMetadata<any>[] {
    return this.domainTemplate.mapDomainFormMetadata(entity);
  }

  get(id: number): Observable<E> {
    console.debug('get() ->');
    return this.entityService.get(id)
      .map(entity => {
        this.buildFormControlGroup(entity);
        return entity;
      })
  }

  save(entity: E): void {
    console.debug('save() ->');
    this.entityService.save(entity).subscribe(
      result => this.goBack()
    );
  }

  update(entity: E): void {
    console.debug('update() ->');
    this.entityService.update(entity).subscribe(
      result => this.goBack()
    );
  }

  deleteEntity(entity: E): void {
    console.debug('deleteEntity() ->');
    this.entityService.delete(entity);
  }

  onSubmit() {
    console.debug('onSubmit() ->', this.form.value);
    let model = <E>this.form.value;
    if (this.model && this.model.id) {
      console.debug('onSubmit() -> update');
      model.id = this.model.id;
      this.update(model);
    } else {
      console.debug('onSubmit() -> save');
      this.save(model);
    }
  }

  /**
   * Go back
   */
  goBack(): void {
    // TODO: find proper implementation for navigation back, now it is hardcoded
    this.router.navigate([this.domainTemplate.getTableUrl()]);
  }

  ngOnDestroy() {
    console.debug('getAll() ->');
    this.sub.unsubscribe();
  }

}
