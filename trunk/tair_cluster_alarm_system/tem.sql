DROP TABLE IF EXISTS groupinfo ;
CREATE TABLE IF NOT EXISTS groupinfo
(
Gid int primary key auto_increment,
DomainA char(255) not null,
IPA char(15),
PortA int not null,
DomainB char(255) ,
IPB char(15),
PortB int,
GroupName char(64),
Description char(255) CHARACTER SET 'utf8',
TairRelease char(8),
Scene char(16),
MonitorURLBase char(255),
WikiURL char(255)
);

DROP TABLE IF EXISTS maillist ;
CREATE TABLE IF NOT EXISTS maillist
(
id int primary key auto_increment,
role char(16) not null ,
mail char(255) not null
);

insert into maillist (role,mail) values(
'developer',
'ganyu.hfl@taobao.com'
);

insert into maillist (role,mail) values(
'developer',
'fenglai.zf@taobao.com'
);

insert into maillist (role,mail) values(
'developer',
'xinshu.wzx@taobao.com'
);

insert into maillist (role,mail) values(
'developer',
'yexiang.ych@taobao.com'
);

insert into maillist (role,mail) values(
'developer',
'zongdai@taobao.com'
);

insert into maillist (role,mail) values(
'developer',
'chucai@taobao.com'
);

insert into maillist (role,mail) values(
'developer',
'yangle.pt@taobao.com'
);

insert into maillist (role,mail) values(
'project enginer',
'xuyuan@taobao.com'
);

insert into groupinfo (DomainA,IPA,PortA,GroupName,TairRelease,Scene,MonitorURLBase,WikiURL) values(
'mcomm-tair1.vip.cm3.tbsite.net',
'172.23.27.11',
5198,
'group_comm',
'2.3',
'Online',
'http://110.75.14.61/tair2/group_comm/',
'http://baike.corp.taobao.com/index.php/Cluster/mcomm'
);

insert into groupinfo (DomainA,IPA,PortA,GroupName,TairRelease,Scene,MonitorURLBase,WikiURL) values(
'snscount-new-tair1.host.cm4.tbsite.net',
'172.24.66.64',
5198,
'group_count',
'2.3',
'Online',
'http://110.75.14.61/tair2/group_count/',
'http://baike.corp.taobao.com/index.php/Cluster/snscount'
);

insert into groupinfo (DomainA,IPA,PortA,GroupName,TairRelease,Scene,MonitorURLBase,WikiURL) values(
'tbsession-tair1.host.cm3.tbsite.net',
'172.23.16.225',
5198,
'group_session',
'2.3',
'Online',
'http://110.75.14.61/tair2/group_session/',
'http://baike.corp.taobao.com/index.php/Cluster/tbsession'
);

insert into groupinfo (DomainA,IPA,PortA,GroupName,TairRelease,Scene,MonitorURLBase,WikiURL) values(
'trade-tair1.vip.cm3.tbsite.net',
'172.23.27.14',
5198,
'group_1',
'2.3',
'Online',
'http://110.75.14.61/tair2/group_1/',
'http://baike.corp.taobao.com/index.php/Cluster/trade'
);

insert into groupinfo (DomainA,IPA,PortA,GroupName,TairRelease,Scene,MonitorURLBase,WikiURL) values(
'mcomm-tair1.vip.cm3.tbsite.net',
'172.23.27.11',
5198,
'group_session',
'2.3',
'Online',
'http://110.75.14.61/tair2/group_session_webww/',
'http://baike.corp.taobao.com/index.php/Cluster/webww'
);

insert into groupinfo (DomainA,IPA,PortA,GroupName,TairRelease,Scene,MonitorURLBase,WikiURL) values(
'tfs-dtair1.vip.cm4.tbsite.net',
'172.24.66.94',
5198,
'group_tfs',
'2.3',
'Online',
'http://110.75.14.61/tair2/group_tfs/',
'http://baike.corp.taobao.com/index.php/Cluster/tfs'
);

insert into groupinfo (DomainA,IPA,PortA,GroupName,TairRelease,Scene,MonitorURLBase,WikiURL) values(
'kdbcommon-dtair1.vip.cm3.tbsite.net',
'172.23.27.41',
5198,
'group_kdbcomm',
'2.3',
'Online',
'http://110.75.14.61/tair2/group_kdbcomm/',
'http://baike.corp.taobao.com/index.php/Cluster/kdbcommon'
);

insert into groupinfo (DomainA,IPA,PortA,GroupName,TairRelease,Scene,MonitorURLBase,WikiURL) values(
'kdbtc-dtair1.vip.cm3.tbsite.net',
'172.23.27.36',
5198,
'group_kdbtc',
'2.3',
'Online',
'http://110.75.14.61/tair2/group_kdbtc/',
'http://baike.corp.taobao.com/index.php/Cluster/kdbtc'
);






insert into groupinfo (DomainA,IPA,PortA,GroupName,TairRelease,Scene,MonitorURLBase,WikiURL) values(
'tdbm2config1.config-host.taobao.com',
'172.23.13.10',
5198,
'group1',
'2.2',
'Online',
'http://110.75.14.61/tair2/2_2_group1/',
'http://baike.corp.taobao.com/index.php/Cluster/group1'
);

insert into groupinfo (DomainA,IPA,PortA,GroupName,TairRelease,Scene,MonitorURLBase,WikiURL) values(
'tdbm2config1.config-host.taobao.com',
'172.23.13.10',
5198,
'group2',
'2.2',
'Online',
'http://110.75.14.61/tair2/2_2_group2',
'http://baike.corp.taobao.com/index.php/Cluster/group2'
);

insert into groupinfo (DomainA,IPA,PortA,GroupName,TairRelease,Scene,MonitorURLBase,WikiURL) values(
'tdbm2config1.config-host.taobao.com',
'172.23.13.10',
5198,
'group3',
'2.2',
'Online',
'http://110.75.14.61/tair2/2_2_group3',
'http://baike.corp.taobao.com/index.php/Cluster/group3'
);

insert into groupinfo (DomainA,IPA,PortA,GroupName,TairRelease,Scene,MonitorURLBase,WikiURL) values(
'item-tair2config1.config-host.taobao.com',
'172.23.27.1',
5198,
'group_item',
'2.2',
'Online',
'http://110.75.14.61/tair2/2_2_group_item/',
'http://baike.corp.taobao.com/index.php/Cluster/item'
);

insert into groupinfo (DomainA,IPA,PortA,GroupName,TairRelease,Scene,MonitorURLBase,WikiURL) values(
'monitor-tair1.host.cm3.tbsite.net',
'',
5198,
'group1',
'2.2',
'Online',
'http://110.75.14.61/tair2/2_2_habo_group1/',
'http://baike.corp.taobao.com/index.php/Cluster/monitor'
);

insert into groupinfo (DomainA,IPA,PortA,GroupName,TairRelease,Scene,MonitorURLBase,WikiURL) values(
'snstdbm1.config-vip.taobao.com',
'172.24.13.28',
5198,
'group11',
'2.2',
'Online',
'http://110.75.14.61/tair2/2_2_group11/',
'http://baike.corp.taobao.com/index.php/Cluster/snsgroup11'
);

insert into groupinfo (DomainA,IPA,PortA,GroupName,TairRelease,Scene,MonitorURLBase,WikiURL) values(
'snstdbm1.config-vip.taobao.com',
'172.24.13.28',
5198,
'group12',
'2.2',
'Online',
'http://110.75.14.61/tair2/2_2_group12/',
'http://baike.corp.taobao.com/index.php/Cluster/snsgroup12'
);

insert into groupinfo (DomainA,IPA,PortA,GroupName,TairRelease,Scene,MonitorURLBase,WikiURL) values(
'snstdbm1.config-vip.taobao.com',
'172.24.13.28',
5198,
'group_sns_core',
'2.2',
'Online',
'http://110.75.14.61/tair2/2_2_group_sns_core/',
'http://baike.corp.taobao.com/index.php/Cluster/snscore'
);

insert into groupinfo (DomainA,IPA,PortA,GroupName,TairRelease,Scene,MonitorURLBase,WikiURL) values(
'snstdbm1.config-vip.taobao.com',
'172.24.13.28',
5198,
'group_1',
'2.2',
'Online',
'http://110.75.14.61/tair2/2_2_sns_group_1/',
'http://baike.corp.taobao.com/index.php/Cluster/snscomm'
);

insert into groupinfo (DomainA,IPA,PortA,GroupName,TairRelease,Scene,MonitorURLBase,WikiURL) values(
'seckill-tair1.vip.cm3.tbsite.net',
'',
5198,
'group1',
'2.2',
'Online',
'http://110.75.14.61/tair2/2_2_seckill_group1/',
'http://baike.corp.taobao.com/index.php/Cluster/seckill'
);

insert into groupinfo (DomainA,IPA,PortA,GroupName,TairRelease,Scene,MonitorURLBase,WikiURL) values(
'172.23.22.16',
'172.23.22.16',
5198,
'group_1',
'2.2',
'Online',
'http://110.75.14.61/tair2/2_2_group_1/',
'http://baike.corp.taobao.com/index.php/Cluster/top'
);