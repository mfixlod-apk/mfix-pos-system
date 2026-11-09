(() => {
  if (window.top !== window || document.getElementById("mfix-touch-host")) return;

  const host = document.createElement("div");
  host.id = "mfix-touch-host";
  host.style.cssText = "all:initial;position:fixed;z-index:2147483647;inset:auto 18px 18px auto;";
  const shadow = host.attachShadow({mode:"closed"});

  const style = document.createElement("style");
  style.textContent = `
    :host{all:initial}
    .launch{font:700 15px Arial,sans-serif;border:0;border-radius:999px;padding:15px 20px;background:#172554;color:#fff;box-shadow:0 8px 30px #0f172a55;cursor:pointer;direction:rtl}
    .frame{display:none;position:fixed;inset:0;width:100vw;height:100dvh;border:0;background:#f4f6fa;z-index:2147483647}
    .frame.open{display:block}
    @media(max-width:600px){.launch{padding:13px 16px;font-size:14px}}
  `;
  const launch = document.createElement("button");
  launch.className = "launch";
  launch.type = "button";
  launch.textContent = "🛒 פתיחת קופת MFIX";
  launch.setAttribute("aria-label","פתיחת קופת MFIX");

  const frame = document.createElement("iframe");
  frame.className = "frame";
  frame.title = "MFIX Touch POS";
  frame.allow = "clipboard-read; clipboard-write";

  const base = chrome.runtime.getURL("pos.html");
  frame.src = base;

  launch.addEventListener("click", () => {
    frame.classList.add("open");
    launch.style.display = "none";
  });

  window.addEventListener("message", event => {
    if (event.source !== frame.contentWindow || event.data?.type !== "MFIX_POS_CLOSE") return;
    frame.classList.remove("open");
    launch.style.display = "block";
  });

  shadow.append(style, launch, frame);
  document.documentElement.appendChild(host);
})();