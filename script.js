let batchSize=25,shuffleQ=false,shuffleA=false,randomMode=false;
let ALL_QUESTIONS=[];
let quizQs=[],cur=0,answered=false,okCount=0,badCount=0;
let history=[];

const dataFiles=[
    'chuong 1_shuffled.json',
    'chuong 2_shuffled.json',
    'tong on_shuffled.json'
];

async function initData(){
    try{
        const promises=dataFiles.map(file=>fetch(`data/${file}`).then(res=>res.json()));
        const results=await Promise.all(promises);
        let totalCount=0;
        const sel=$('range-sel');
        sel.innerHTML='<option value="all">Tất cả các chương</option>';
        results.forEach((data,index)=>{
            const sourceName=dataFiles[index].replace('_shuffled.json','');
            sel.innerHTML+=`<option value="${index}">${sourceName} (${data.length} câu)</option>`;
            data.forEach(q=>{
                q.sourceIdx=index;
                ALL_QUESTIONS.push(q);
            });
            totalCount+=data.length;
        });
        $('total-count').textContent=totalCount+' CÂU';
    }catch(err){
        alert("Khong the tai du lieu JSON. Hay chac chan ban da dat cac file _shuffled.json vao thu muc data/ va dang chay tren server.");
    }
}

function $(id){return document.getElementById(id);}

function shuffle(a){
    a=[...a];
    for(let i=a.length-1;i>0;i--){
        const j=Math.floor(Math.random()*(i+1));
        [a[i],a[j]]=[a[j],a[i]];
    }
    return a;
}

function showScreen(id){
    document.querySelectorAll('.screen').forEach(s=>s.classList.remove('active'));
    $(id).classList.add('active');
    if(id==='home')$('theme-toggle').style.display='';
    else $('theme-toggle').style.display='none';
    window.scrollTo(0,0);
}

document.querySelectorAll('.mode-tab').forEach(tab=>{
    tab.addEventListener('click',()=>{
        document.querySelectorAll('.mode-tab').forEach(t=>t.classList.remove('active'));
        tab.classList.add('active');
        randomMode=tab.dataset.mode==='rand';
        if(randomMode){
            shuffleQ=true;
            shuffleA=true;
            $('shuf-q').checked=true;
            $('shuf-a').checked=true;
        }else{
            shuffleQ=false;
            shuffleA=false;
            $('shuf-q').checked=false;
            $('shuf-a').checked=false;
        }
    });
});

document.querySelectorAll('.count-btn').forEach(btn=>{
    btn.addEventListener('click',()=>{
        document.querySelectorAll('.count-btn').forEach(b=>b.classList.remove('active'));
        btn.classList.add('active');
        if(btn.dataset.count==='all')batchSize=Infinity;
        else batchSize=parseInt(btn.dataset.count);
    });
});

$('shuf-q').addEventListener('change',e=>shuffleQ=e.target.checked);
$('shuf-a').addEventListener('change',e=>shuffleA=e.target.checked);

function startQuiz(){
    const rangeVal=$('range-sel').value;
    let pool;
    if(rangeVal==='all')pool=[...ALL_QUESTIONS];
    else pool=ALL_QUESTIONS.filter(q=>q.sourceIdx===parseInt(rangeVal));
    if(pool.length===0){
        alert("Khong co du lieu cho phan nay!");
        return;
    }
    let batch;
    const actualSize=batchSize>pool.length?pool.length:batchSize;
    if(randomMode)batch=shuffle(pool).slice(0,actualSize);
    else{
        batch=pool.slice(0,actualSize);
        if(shuffleQ)batch=shuffle(batch);
    }
    quizQs=batch.map(q=>{
        let opts=[{key:'a',text:q.a},{key:'b',text:q.b},{key:'c',text:q.c},{key:'d',text:q.d}];
        if(shuffleA)opts=shuffle(opts);
        return {num:q.num,question:q.q,opts,ans:q.ans};
    });
    cur=0;
    okCount=0;
    badCount=0;
    answered=false;
    history=[];
    showScreen('quiz');
    renderQ();
}

$('btn-start').addEventListener('click',startQuiz);
$('btn-back').addEventListener('click',()=>showScreen('home'));
$('btn-home2').addEventListener('click',()=>showScreen('home'));
$('btn-retry').addEventListener('click',startQuiz);

function renderQ(){
    const q=quizQs[cur];
    const total=quizQs.length;
    const pos=cur+1;
    $('qc-cur').textContent=pos;
    $('qc-tot').textContent=total;
    $('sc-ok').textContent=okCount;
    $('sc-bad').textContent=badCount;
    $('pbar').style.width=((pos-1)/total*100)+'%';
    $('q-text').textContent=q.question;
    const posLabels=['A','B','C','D'];
    const container=$('options');
    container.innerHTML='';
    q.opts.forEach((opt,i)=>{
        const btn=document.createElement('button');
        btn.className='opt-btn';
        btn.dataset.key=opt.key;
        btn.style.animationDelay=(i*0.06)+'s';
        btn.innerHTML=`<span class="opt-key">${posLabels[i]}</span><span class="opt-text">${opt.text}</span>`;
        btn.addEventListener('click',()=>answer(opt.key,q));
        container.appendChild(btn);
    });
    $('btn-next').className='next-btn';
    $('btn-next').textContent='Câu tiếp theo →';
    answered=false;
}

function answer(selected,q){
    if(answered)return;
    answered=true;
    const isOk=(selected===q.ans);
    if(isOk)okCount++;
    else badCount++;
    $('sc-ok').textContent=okCount;
    $('sc-bad').textContent=badCount;
    const selectedOpt=q.opts.find(o=>o.key===selected);
    const correctOpt=q.opts.find(o=>o.key===q.ans);
    history.push({
        num:q.num,
        question:q.question,
        selectedKey:selected,
        selectedText:selectedOpt?selectedOpt.text:'',
        correctKey:q.ans,
        correctText:correctOpt?correctOpt.text:'',
        isOk:isOk
    });
    $('options').querySelectorAll('.opt-btn').forEach(btn=>{
        btn.disabled=true;
        const keyEl=btn.querySelector('.opt-key');
        const key=btn.dataset.key;
        if(key===q.ans){
            btn.classList.add('state-correct');
            keyEl.textContent='✓';
        }else if(key===selected&&!isOk){
            btn.classList.add('state-wrong');
            keyEl.textContent='✗';
        }
    });
    const nextBtn=$('btn-next');
    nextBtn.classList.add('show');
    if(cur===quizQs.length-1)nextBtn.textContent='Xem kết quả →';
}

$('btn-next').addEventListener('click',()=>{
    if(cur<quizQs.length-1){
        cur++;
        renderQ();
        window.scrollTo({top:0,behavior:'smooth'});
    }else showResult();
});

let activeFilter='all';

function escHtml(s){
    return s.replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;');
}

function buildReviewList(filter){
    activeFilter=filter;
    document.querySelectorAll('.f-tab').forEach(t=>t.classList.toggle('active',t.dataset.filter===filter));
    let items;
    if(filter==='all')items=history;
    else if(filter==='ok')items=history.filter(h=>h.isOk);
    else items=history.filter(h=>!h.isOk);
    const container=$('review-list');
    container.innerHTML='';
    if(items.length===0){
        container.innerHTML=`<div style="text-align:center;color:var(--text-muted);padding:40px 0;font-size:14px">Không có câu nào</div>`;
        return;
    }
    items.forEach((item,idx)=>{
        const div=document.createElement('div');
        div.className='rev-item '+(item.isOk?'item-ok':'item-bad');
        div.style.animationDelay=(idx*0.03)+'s';
        div.style.animation='fadeUp .25s ease both';
        let answersHtml=`<div class="rev-ans-row ${item.isOk?'row-ok':'row-bad'}"><span class="rev-text">${escHtml(item.selectedText)}</span><span class="rev-icon">${item.isOk?'✓':'✗'}</span></div>`;
        if(!item.isOk)answersHtml+=`<div class="rev-ans-row row-ok"><span class="rev-text">${escHtml(item.correctText)}</span><span class="rev-icon">✓</span></div>`;
        div.innerHTML=`<div class="rev-top"><span class="rev-num">Câu ${item.num}</span><span class="rev-status ${item.isOk?'sok':'sbad'}">${item.isOk?'✓ Đúng':'✗ Sai'}</span></div><div class="rev-q">${escHtml(item.question)}</div><div class="rev-answers">${answersHtml}</div>`;
        container.appendChild(div);
    });
}

document.querySelectorAll('.f-tab').forEach(tab=>{
    tab.addEventListener('click',()=>buildReviewList(tab.dataset.filter));
});

(function(){
    const html=document.documentElement;
    const saved=localStorage.getItem('theme');
    function applyTheme(theme){
        if(theme==='light')html.setAttribute('data-theme','light');
        else html.removeAttribute('data-theme');
    }
    applyTheme(saved||'dark');
    $('theme-toggle').addEventListener('click',()=>{
        let next;
        if(html.getAttribute('data-theme')==='light')next='dark';
        else next='light';
        localStorage.setItem('theme',next);
        applyTheme(next);
    });
})();

function showResult(){
    const total=quizQs.length;
    const pct=Math.round(okCount/total*100);
    let emoji,title;
    if(pct>=90){
        emoji='🏆';
        title='Xuất sắc!';
    }else if(pct>=75){
        emoji='🎯';
        title='Hoàn thành tốt!';
    }else if(pct>=60){
        emoji='📖';
        title='Khá tốt!';
    }else if(pct>=40){
        emoji='💪';
        title='Cần cố gắng thêm!';
    }else{
        emoji='🔄';
        title='Tiếp tục ôn luyện!';
    }
    $('res-emoji').textContent=emoji;
    $('res-title').textContent=title;
    $('res-desc').textContent=`Hoàn thành ${total} câu · ${okCount} đúng, ${badCount} sai`;
    $('res-ok').textContent=okCount;
    $('res-bad').textContent=badCount;
    $('res-pct').textContent=pct+'%';
    let wrongCount=0;
    history.forEach(h=>{if(!h.isOk)wrongCount++;});
    $('rh-counts').textContent=`${total} câu · ${wrongCount} câu sai`;
    buildReviewList('all');
    showScreen('result');
}

initData();