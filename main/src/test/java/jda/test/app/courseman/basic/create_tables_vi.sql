--------------------------------------------
--------- CONFIGURATION SETTINGS -----------
--------------------------------------------
create schema vi;
drop table vi.regionstyle;
drop table vi.region;
drop table vi.style;

-- region table
create table vi.region (
  id smallint constraint region_pk primary key,
  name varchar(50) DEFAULT null,
  label varchar(255),
  imageicon varchar(50)	DEFAULT null, -- image icon file name on disk
  parentid smallint,
  width smallint, 
  height smallint,
  type varchar(10), -- used for item region, e.g. option menu, text field, etc. 
  defvalue varchar(30), -- default value, used for text and choice/radio menu items 
  constraint regionfk_1 
    foreign key (parentid) references vi.region(id) 
    on delete cascade on update restrict
);

-- style table
create table vi.style (
  id smallint constraint style_pk primary key,
  font varchar(30) DEFAULT null,
  bgcolor varchar(20) DEFAULT null,
  fgcolor varchar(20) DEFAULT null
);

-- region style table
create table vi.regionstyle(
  regionid smallint,
  styleid smallint,
  constraint regionstylefk_1 
    foreign key (regionid) references vi.region(id) 
    on delete cascade on update restrict,
  constraint regionstylefk_2 
    foreign key (styleid) references vi.style(id)
    on delete cascade on update restrict,
  constraint regionstylepk primary key(regionid,styleid)
);
