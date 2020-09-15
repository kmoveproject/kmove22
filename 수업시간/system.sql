DROP TABLE DEPT;
CREATE TABLE DEPT
       (DEPTNO NUMBER(2) CONSTRAINT PK_DEPT PRIMARY KEY,
	DNAME VARCHAR2(14) ,
	LOC VARCHAR2(13) ) ;
DROP TABLE EMP;
CREATE TABLE EMP
       (EMPNO NUMBER(4) CONSTRAINT PK_EMP PRIMARY KEY,
	ENAME VARCHAR2(10),
	JOB VARCHAR2(9),
	MGR NUMBER(4),
	HIREDATE DATE,
	SAL NUMBER(7,2),
	COMM NUMBER(7,2),
	DEPTNO NUMBER(2) CONSTRAINT FK_DEPTNO REFERENCES DEPT);
INSERT INTO DEPT VALUES
	(10,'ACCOUNTING','NEW YORK');
INSERT INTO DEPT VALUES (20,'RESEARCH','DALLAS');
INSERT INTO DEPT VALUES
	(30,'SALES','CHICAGO');
INSERT INTO DEPT VALUES
	(40,'OPERATIONS','BOSTON');
INSERT INTO EMP VALUES
(7369,'SMITH','CLERK',7902,to_date('17-12-1980','dd-mm-yyyy'),800,NULL,20);
INSERT INTO EMP VALUES
(7499,'ALLEN','SALESMAN',7698,to_date('20-2-1981','dd-mm-yyyy'),1600,300,30);
INSERT INTO EMP VALUES
(7521,'WARD','SALESMAN',7698,to_date('22-2-1981','dd-mm-yyyy'),1250,500,30);
INSERT INTO EMP VALUES
(7566,'JONES','MANAGER',7839,to_date('2-4-1981','dd-mm-yyyy'),2975,NULL,20);
INSERT INTO EMP VALUES
(7654,'MARTIN','SALESMAN',7698,to_date('28-9-1981','dd-mm-yyyy'),1250,1400,30);
INSERT INTO EMP VALUES
(7698,'BLAKE','MANAGER',7839,to_date('1-5-1981','dd-mm-yyyy'),2850,NULL,30);
INSERT INTO EMP VALUES
(7782,'CLARK','MANAGER',7839,to_date('9-6-1981','dd-mm-yyyy'),2450,NULL,10);
INSERT INTO EMP VALUES
(7788,'SCOTT','ANALYST',7566,to_date('13-JUL-87')-85,3000,NULL,20);
INSERT INTO EMP VALUES
(7839,'KING','PRESIDENT',NULL,to_date('17-11-1981','dd-mm-yyyy'),5000,NULL,10);
INSERT INTO EMP VALUES
(7844,'TURNER','SALESMAN',7698,to_date('8-9-1981','dd-mm-yyyy'),1500,0,30);
INSERT INTO EMP VALUES
(7876,'ADAMS','CLERK',7788,to_date('13-JUL-87')-51,1100,NULL,20);
INSERT INTO EMP VALUES
(7900,'JAMES','CLERK',7698,to_date('3-12-1981','dd-mm-yyyy'),950,NULL,30);
INSERT INTO EMP VALUES
(7902,'FORD','ANALYST',7566,to_date('3-12-1981','dd-mm-yyyy'),3000,NULL,20);
INSERT INTO EMP VALUES
(7934,'MILLER','CLERK',7782,to_date('23-1-1982','dd-mm-yyyy'),1300,NULL,10);
DROP TABLE BONUS;
CREATE TABLE BONUS
	(
	ENAME VARCHAR2(10)	,
	JOB VARCHAR2(9)  ,
	SAL NUMBER,
	COMM NUMBER
	) ;
DROP TABLE SALGRADE;
CREATE TABLE SALGRADE
      ( GRADE NUMBER,
	LOSAL NUMBER,
	HISAL NUMBER );
INSERT INTO SALGRADE VALUES (1,700,1200);
INSERT INTO SALGRADE VALUES (2,1201,1400);
INSERT INTO SALGRADE VALUES (3,1401,2000);
INSERT INTO SALGRADE VALUES (4,2001,3000);
INSERT INTO SALGRADE VALUES (5,3001,9999);
COMMIT;

select * from emp;
select 10*20 from dual;
select round(45.293, 2)  from DUAL;    --45.29
select round(45.293, -1) from dual;    --50
select sal, mod(sal,100) from emp;     --mod ������ ������
select * from emp where mod(empno, 2)=1;  --empno�� Ȧ���� �͵��� ��� ��ȸ
select sum(sal) from emp;   --sal(�޿�)�� �հ�
select avg(sal) from emp;   --sal(�޿�)�� ���
select max(sal), min(sal) from emp;  --�޿��� �ִ밪��, �ּҰ�
select count(comm) from emp;   --comm�� ���� �����ϴ� ����
select comm from emp where deptno=10; --deptno�� 10�� ���� comm�� ����
select count(comm) from emp where deptno=10;
select distinct job from emp; 
select count(distinct job) from emp; 
select max(sal), ename from emp;  --����(�����Լ�max..���� ename�Բ� ��ȸ�ȵ�
select max(sal) from emp; 
select deptno, count(*), count(comm)
from emp
group by deptno;  --emp���̺���  deptno�� (deptno, �׷캰����, comm�� ����)��ȸ

select deptno, avg(sal) 
from emp 
group by deptno
having avg(sal)>=2000;   
  --having �ڿ� ������ �� (deptno�� sal�� ����� 2000�̻���)
  --group by �� having�� �� �����̴�.
  
select deptno, count(sal)
from emp
where sal >= 1000
group by deptno
having count(sal) >= 3; 
   --(deptno�� sal�� ������ 3�̻�)�� sal>=1000 �� ���� (�μ���ȣ, �޿�����) ��ȸ

select deptno, count(sal)
from emp
group by deptno
having count(sal) >= 3;


select empno, ename, deptno
from emp
where empno=7900;

select dname
from dept
where deptno=30;

select ename, dname
from emp, dept
where emp.deptno = dept.deptno;

select ename, deptno
from emp;

select deptno, dname
from dept;

select e.ename, d.dname, e.deptno, d.deptno
from emp e, dept d
where e.deptno=d.deptno;

--����̸��� SMITH �� �����(ename), �μ���(dname), �μ��ڵ�(deptno)�� ����Ͻÿ�

select emp.ename, dept.dname, dept.deptno
from emp, dept
where emp.ename='SMITH' and emp.deptno=dept.deptno;


select e.ename, e.sal, s.grade
from emp e, salgrade s
where e.sal >= s.losal and e.sal <= s.hisal;

select grade, losal, hisal
from salgrade;

select * from salgrade;
select * from emp;

select e.ename || '�� �Ŵ�����' || m.ename || '�Դϴ�.'
from emp e, emp m
where e.mgr = m.empno(+);   --�����ܺ�����(������ ������, �������� ��ġ�ϴ� �͸�)

select ename, empno, mgr
from emp;
