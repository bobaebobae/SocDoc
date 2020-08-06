<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<% String ctxPath = request.getContextPath(); %>

<style type="text/css">

	h2 {
		font-weight: bold;
		padding-top: 30px;
	}
	
    
    table {
		width: 100%;
		border-collapse: collapse;
		margin-top: 25px;
	}
	
    th {
		font-size: 14px;
	    font-weight: bold;
	    color: #222222;
	    text-align: center;
	    padding: 17px 3px;
	    border-top: 1px solid #333333;
	    border-bottom: 1px solid #333333;
	}
	
	td {
		font-size: 14px;
	    color: #666666;
	    text-align: center;
	    padding: 17px 0;
	    border-bottom: 1px solid #dddddd;
	    line-height: 1.8;
	
	}
	
	th {
		text-align: center;
	}
	
	#faqList {
		width: 1000px;
		margin-left: auto;
		margin-right: auto;
	}
	
	#faqList:after {
		content: '';
		clear: both;
		display: block;
	}
	
	.faqTbl{
      width:100%;   
      margin: 30px 0;   
   }   
   
   td{
      border-top: 1px solid #999999;
      border-bottom: 1px solid #999999; 
      padding:25px !important;      
   }
   
   .answer {      
      background-color: #e6f5ff;
   }
   
   .addBtn {
      background-color: #0080ff;
      color:#fff;
      cursor: pointer;   
      border: 1px solid #dddddd;       
      padding: 0.25em .75em;    
      border-radius: .25em;       
      font-weight: 500;
      font-size: 10pt;   
   }
   
   .deleteBtn {
      background-color: #efefef;
      cursor: pointer;   
      border: 1px solid #dddddd;       
      padding: 0.25em .75em;    
      border-radius: .25em;       
      font-weight: 500;
      font-size: 10pt;   
      margin-right: 1
   }
	
    #ckAll {
      background-color: #efefef;
      cursor: pointer;   
      float: left;
      border: 1px solid #dddddd;       
      padding: 0.25em .75em;    
      border-radius: .25em;       
      font-weight: 500;
      font-size: 10pt;   
      margin-right: 1
    }


</style>

	<div id="container" style="min-height: 70vh;">
		
		<div id="faqList">
		
		   <h2>FAQ관리</h2>
				
		   <table class="faqTbl">
		      <tr class="question">
		         <td><input type="checkbox" /></td>
		         <td >Q . 속닥속닥은 병원인가요?</td>
		      </tr>
		      <tr class="answer">
		         <td></td>
		         <td>A . 주식회사 속닥속닥은 의료정보의 중개서비스 또는 의료정보중개시스템의 제공자로서, 
		            의료정보의 당사자가 아니며, 의료정보와 관련된 의무와 책임은 각 의료기관에게 있습니다.</td>
		      </tr>      
		      <tr class="question">
		         <td><input type="checkbox" /></td>
		         <td >Q . 속닥속닥은 뭐하는 병원인가요?</td>
		      </tr>
		      <tr class="answer">
		         <td></td>         
		         <td>A . 주식회사 속닥속닥은 병원이 아니라고요.</td>
		      </tr>
		   </table>
		   
		   <button type="button" id="ckAll" class="ckAll"> 전체선택 </button>
		   
		   <div align="right">
		      <button type="button" id="addBtn" class="addBtn" onclick="location.href='<%= ctxPath%>/faqWrite.sd'"> 추가 </button>
		      <button type="button" id="deleteBtn" class="deleteBtn"> 삭제 </button>   
		   </div>
            
		</div>

	
	</div>