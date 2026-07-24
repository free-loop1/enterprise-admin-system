use enterprise_admin;

show tables from enterprise_admin;

describe enterprise_admin.sys_user;

show create table enterprise_admin.sys_user;

show index from enterprise_admin.sys_user;

select constraint_name,
       constraint_type
from information_schema.table_constraints
where table_schema ='enterprise_admin'
    and table_name='sys_user'
order by constraint_type,constraint_name;