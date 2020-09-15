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
select sal, mod(sal,100) from emp;     --mod 나누어 나머지
select * from emp where mod(empno, 2)=1;  --empno가 홀수인 것들을 모두 조회
select sum(sal) from emp;   --sal(급여)의 합계
select avg(sal) from emp;   --sal(급여)의 평균
select max(sal), min(sal) from emp;  --급여의 최대값과, 최소값
select count(comm) from emp;   --comm에 값이 존재하는 갯수
select comm from emp where deptno=10; --deptno가 10인 것의 comm의 갯수
select count(comm) from emp where deptno=10;
select distinct job from emp; 
select count(distinct job) from emp; 
select max(sal), ename from emp;  --에러(집단함수max..사용시 ename함께 조회안됨
select max(sal) from emp; 
select deptno, count(*), count(comm)
from emp
group by deptno;  --emp테이블에서  deptno별 (deptno, 그룹별갯수, comm의 갯수)조회

select deptno, avg(sal) 
from emp 
group by deptno
having avg(sal)>=2000;   
  --having 뒤에 조건이 옴 (deptno별 sal의 평균이 2000이상인)
  --group by 와 having은 한 묶음이다.
  
select deptno, count(sal)
from emp
where sal >= 1000
group by deptno
having count(sal) >= 3; 
   --(deptno별 sal의 갯수가 3이상)인 sal>=1000 인 것의 (부서번호, 급여갯수) 조회

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

--사원이름이 SMITH 인 사원명(ename), 부서명(dname), 부서코드(deptno)를 출력하시오

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

select e.ename || '의 매니저는' || m.ename || '입니다.'
from emp e, emp m
where e.mgr = m.empno(+);   --좌측외부조인(왼쪽은 모두출력, 오른쪽은 일치하는 것만)

select ename, empno, mgr
from emp;
