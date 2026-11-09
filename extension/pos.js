const $ = id => document.getElementById(id);
const money = n => new Intl.NumberFormat("he-IL",{style:"currency",currency:"ILS"}).format(Number(n)||0);
const defaults = [
  {id:"demo-1",name:"מטען USB-C מהיר 25W",price:49.9,category:"מטענים",sku:"CH-025",emoji:"🔌"},
  {id:"demo-2",name:"כבל Type-C 1 מטר",price:19.9,category:"כבלים",sku:"CB-TC1",emoji:"🔗"},
  {id:"demo-3",name:"כיסוי iPhone שקוף",price:39.9,category:"כיסויים",sku:"CS-IP",emoji:"📱"},
  {id:"demo-4",name:"מגן מסך",price:25,category:"מגני מסך",sku:"SP-01",emoji:"🛡️"},
  {id:"demo-5",name:"אוזניות Bluetooth",price:79.9,category:"אוזניות",sku:"AU-BT",emoji:"🎧"},
  {id:"demo-6",name:"ראש טעינה 20W",price:59.9,category:"מטענים",sku:"CH-020",emoji:"⚡"}
];
let products = [], cart = [], category = "הכל", favorites = new Set();

async function load(){
  const saved = await chrome.storage.local.get(["mfixTouchProducts","mfixTouchFavorites"]);
  products = saved.mfixTouchProducts || defaults;
  favorites = new Set(saved.mfixTouchFavorites || []);
  renderCategories(); renderProducts(); renderCart();
}
function categories(){
  return ["הכל","מועדפים",...new Set(products.map(p=>p.category).filter(Boolean))];
}
function renderCategories(){
  $("categories").innerHTML = categories().map(c=>`<button class="category ${category===c?"active":""}" data-category="${escapeHtml(c)}">${c==="מועדפים"?"★ ":""}${escapeHtml(c)}</button>`).join("");
}
function renderProducts(){
  const q = $("search").value.trim().toLowerCase();
  const shown = products.filter(p=>(category==="הכל" || (category==="מועדפים"?favorites.has(p.id):p.category===category)) && [p.name,p.sku,p.category].join(" ").toLowerCase().includes(q));
  $("products").innerHTML = shown.map(p=>`<button class="product" data-add="${escapeHtml(p.id)}"><span class="emoji">${p.emoji||"📦"}</span><strong>${escapeHtml(p.name)}</strong><small>${escapeHtml(p.sku||p.category||"")}</small><span class="price">${money(p.price)}</span></button>`).join("") || '<div class="empty">לא נמצאו מוצרים. אפשר להוסיף מוצר דרך ניהול מוצרים.</div>';
}
function renderCart(){
  const count = cart.reduce((n,p)=>n+p.qty,0), total = cart.reduce((n,p)=>n+p.qty*p.price,0);
  $("item-count").textContent = count+" פריטים";
  $("subtotal").textContent = money(total); $("total").textContent = money(total);
  $("cart-items").innerHTML = cart.length ? cart.map(p=>`<div class="cart-row"><div><strong>${escapeHtml(p.name)}</strong><small>${money(p.price)} ליחידה</small><div class="qty"><button data-dec="${escapeHtml(p.id)}" aria-label="הפחתת כמות">−</button><b>${p.qty}</b><button data-inc="${escapeHtml(p.id)}" aria-label="הגדלת כמות">+</button></div></div><span class="line-price">${money(p.price*p.qty)}</span></div>`).join("") : '<div class="empty">הסל ריק כרגע</div>';
}
function escapeHtml(value){return String(value??"").replace(/[&<>"']/g,c=>({"&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#39;"}[c]));}
function toast(msg){$("toast").textContent=msg;$("toast").classList.add("show");setTimeout(()=>$("toast").classList.remove("show"),2200);}
$("categories").addEventListener("click",e=>{const b=e.target.closest("[data-category]");if(!b)return;category=b.dataset.category;renderCategories();renderProducts();});
$("products").addEventListener("click",e=>{const b=e.target.closest("[data-add]");if(!b)return;const p=products.find(x=>x.id===b.dataset.add);if(!p)return;const row=cart.find(x=>x.id===p.id);row?row.qty++:cart.push({...p,qty:1});renderCart();});
$("cart-items").addEventListener("click",e=>{const inc=e.target.closest("[data-inc]"),dec=e.target.closest("[data-dec]");if(inc){const p=cart.find(x=>x.id===inc.dataset.inc);if(p)p.qty++;}if(dec){const p=cart.find(x=>x.id===dec.dataset.dec);if(p)p.qty--;cart=cart.filter(x=>x.qty>0);}renderCart();});
$("search").addEventListener("input",renderProducts);
$("clear-cart").addEventListener("click",()=>{cart=[];renderCart();});
$("back-to-site").addEventListener("click",()=>window.parent.postMessage({type:"MFIX_POS_CLOSE"},"*"));
$("manage-products").addEventListener("click",()=>$("modal").classList.remove("hidden"));
$("close-modal").addEventListener("click",()=>$("modal").classList.add("hidden"));
$("modal").addEventListener("click",e=>{if(e.target===$("modal"))$("modal").classList.add("hidden");});
$("product-form").addEventListener("submit",async e=>{e.preventDefault();const f=new FormData(e.currentTarget);const p={id:"custom-"+crypto.randomUUID(),name:f.get("name").trim(),price:Number(f.get("price")),category:f.get("category").trim()||"אחר",sku:f.get("sku").trim(),emoji:"📦"};products.push(p);await chrome.storage.local.set({mfixTouchProducts:products});e.currentTarget.reset();$("modal").classList.add("hidden");category="הכל";renderCategories();renderProducts();toast("המוצר נשמר בתוסף");});
$("copy-sale").addEventListener("click",async()=>{if(!cart.length){toast("הסל ריק");return;}const payload={source:"MFIX Touch POS",items:cart.map(({id,name,sku,price,qty})=>({id,name,sku,price,quantity:qty})),total:cart.reduce((n,p)=>n+p.qty*p.price,0),createdAt:new Date().toISOString()};try{await navigator.clipboard.writeText(JSON.stringify(payload,null,2));toast("פרטי העסקה הועתקו ללוח בלבד. לא נשלח מסמך.");}catch{toast("לא ניתן להעתיק בדפדפן זה");}});
load();