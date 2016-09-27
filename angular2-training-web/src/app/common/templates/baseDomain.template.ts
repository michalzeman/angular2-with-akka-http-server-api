import {DomainMetadata, FormMetadata} from "./form-metadata";
import {BaseEntity} from "../entities/baseEntity";

export abstract class BaseDomainTemplate<E extends BaseEntity> {

  metadataArray: DomainMetadata[];

  constructor() {
    this.metadataArray = this.initMetadataArray();
  }

  protected abstract initMetadataArray(): DomainMetadata[];

  abstract mapDomainFormMetadata(entity: E): FormMetadata<any>[];

  abstract getDetailUrl():string;

  abstract getTableUrl():string;

  abstract getPropertiesLables():string;

  abstract getTableTitle(): string;
}
