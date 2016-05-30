===============新机型适配流程===============

1.在rom_info.json里面添加对应的机型信息（特征一定要唯一，与其它机型不能一样）

2.在task_info.json里面添加对应的(romid=taskid)task项目,可以有多个,task信息里指名对应的intent_id和action_id,再去完善具体的intent和action
