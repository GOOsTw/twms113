SET @newid=0;
UPDATE inventoryitems SET inventoryitemid=(@newid:=@newid+1) ORDER BY inventoryitemid;
ALTER TABLE inventoryitems AUTO_INCREMENT = 1;

SET @newid=0;
UPDATE queststatus SET queststatusid=(@newid:=@newid+1) ORDER BY queststatusid;
ALTER TABLE queststatus AUTO_INCREMENT = 1;


SET @newid=0;
UPDATE inventoryequipment SET inventoryequipmentid=(@newid:=@newid+1) ORDER BY inventoryequipmentid;
ALTER TABLE inventoryequipment AUTO_INCREMENT = 1;

SET @newid=0;
UPDATE skills SET id=(@newid:=@newid+1) ORDER BY id;
ALTER TABLE skills AUTO_INCREMENT = 1;


SET @newid=0;
UPDATE skills_cooldowns SET id=(@newid:=@newid+1) ORDER BY id;
ALTER TABLE skills_cooldowns AUTO_INCREMENT = 1;


SET @newid=0;
UPDATE savedlocations SET id=(@newid:=@newid+1) ORDER BY id;
ALTER TABLE savedlocations AUTO_INCREMENT = 1;


SET @newid=0;
UPDATE trocklocations SET trockid=(@newid:=@newid+1) ORDER BY trockid;
ALTER TABLE trocklocations AUTO_INCREMENT = 1;


SET @newid=0;
UPDATE skillmacros SET id=(@newid:=@newid+1) ORDER BY id;
ALTER TABLE skillmacros AUTO_INCREMENT = 1;

SET @newid=0;
UPDATE regrocklocations SET trockid=(@newid:=@newid+1) ORDER BY trockid;
ALTER TABLE regrocklocations AUTO_INCREMENT = 1;

SET @newid=0;
UPDATE queststatusmobs SET queststatusmobid=(@newid:=@newid+1) ORDER BY queststatusmobid;
ALTER TABLE queststatusmobs AUTO_INCREMENT = 1;

SET @newid=0;
UPDATE monsterbook SET id=(@newid:=@newid+1) ORDER BY id;
ALTER TABLE monsterbook AUTO_INCREMENT = 1;

SET @newid=0;
UPDATE questinfo SET questinfoid=(@newid:=@newid+1) ORDER BY questinfoid;
ALTER TABLE questinfo AUTO_INCREMENT = 1;

SET @newid=0;
UPDATE csequipment SET inventoryequipmentid=(@newid:=@newid+1) ORDER BY inventoryequipmentid;
ALTER TABLE csequipment AUTO_INCREMENT = 1;

SET @newid=0;
UPDATE csitems SET inventoryitemid=(@newid:=@newid+1) ORDER BY inventoryitemid;
ALTER TABLE csitems AUTO_INCREMENT = 1;