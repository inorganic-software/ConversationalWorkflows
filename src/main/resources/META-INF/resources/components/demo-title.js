import {LitElement, html, css} from 'lit';

export class DemoTitle extends LitElement {

    static styles = css`
        :host {
            display: block;
            margin: 0;
            padding: 0;
        }

        h1 {
            font-family: "Red Hat Mono", monospace;
            font-size: 72px;
            font-weight: 700;
            line-height: 1.1;
            color: #fcb11c;
            margin: 0.3em 0 0.2em 0;
            text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.2);
            background: linear-gradient(90deg, #fcb11c, #fe7800);
            -webkit-background-clip: text;
            background-clip: text;
            color: transparent;
        }

        h2 {
            margin: 0;
            font-size: 28px;
            color: #333;
            font-weight: 500;
            background: linear-gradient(90deg, #4f46e5, #333);
            -webkit-background-clip: text;
            background-clip: text;
            color: transparent;
        }

        .header {
            display: flex;
            align-items: center;
            gap: 10px;
            padding: 0.7em 0 1em 1.5em;
            background: linear-gradient(90deg, rgba(255,255,255,0.3), rgba(252,177,28,0.1));
            margin: 0;
            border-bottom: 1px solid #fcb11c;
        }

        .logo {
            max-width: 70px;
            height: auto;
            transition: transform 0.3s;
        }

        .chatbot-design {
            max-width: 100%;
            height: auto;
            transition: transform 0.3s;
        }

        .logo:hover {
            transform: scale(1.1);
        }

        .brand {
            font-size: 32px;
            font-weight: bold;
            font-family: "Red Hat Mono", monospace;
        }

        .brand .purple {
            color: #4f46e5;
        }

        .brand .mas {
            color: #fe7800;
        }

        .brand .orange {
            color: #000;
        }

        .title {
            text-align: center;
            padding: 0.8em;
            background: linear-gradient(135deg, rgba(255,255,255,0.2), rgba(252,177,28,0.05));
            margin: 0;
            border-radius: 10px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            display: flex;
            flex-direction: column;
            align-items: center;
        }

        .explanation {
            margin: 1em 0 2em 0;
            width: 70%;
            font-size: 22px;
            line-height: 1.8;
            text-align: left;
            padding-left: 1.5em;
            display: block;
        }

        .intro-card {
            padding: 1.5em;
            background: linear-gradient(45deg, #1a1a1a, #333);
            color: #fff;
            border-radius: 12px;
            box-shadow: 0 6px 12px rgba(0, 0, 0, 0.3);
            margin-bottom: 1.5em;
            text-align: center;
            font-weight: 600;
            font-family: 'Red Hat Text', sans-serif;
            animation: fadeIn 1s ease-in;
        }

        @keyframes fadeIn {
            from { opacity: 0; }
            to { opacity: 1; }
        }

        .lists {
            display: flex;
            justify-content: space-between;
            gap: 2em;
        }

        .list-left, .list-right {
            width: 48%;
            padding: 1.5em;
            background: linear-gradient(135deg, #fff, #f9f9f9);
            border-radius: 12px;
            box-shadow: 0 6px 12px rgba(0, 0, 0, 0.1);
            border-left: 4px solid #fcb11c;
        }

        .explanation ul {
            margin: 0.7em 0;
            padding-left: 1.5em;
            list-style-type: disc;
            text-align: left;
        }

        .explanation li {
            margin: 0.5em 0;
            padding-left: 0.7em;
            border-left: 3px solid #fcb11c;
            background: rgba(252, 177, 28, 0.15);
            border-radius: 4px;
            transition: transform 0.2s;
        }

        .explanation li:hover {
            transform: translateX(5px);
        }

        .chatbot-design {
            margin: 2em 30px;
            width: 90%;
            padding: 1.5em;
            background: linear-gradient(135deg, #f9f9f9, #fff);
            border-radius: 12px;
            box-shadow: 0 6px 12px rgba(0, 0, 0, 0.1);
            border-left: 4px solid #4f46e5;
            font-size: 22px;
            line-height: 1.8;
            text-align: left;
            padding-left: 1.5em;
        }

        .chatbot-design h3 {
            font-family: 'Red Hat Mono', monospace;
            font-size: 32px;
            color: #4f46e5;
            margin-bottom: 0.5em;
            background: linear-gradient(90deg, #4f46e5, #333);
            -webkit-background-clip: text;
            background-clip: text;
            color: transparent;
        }

        .chatbot-design img {
            max-width: 100%;
            height: auto;
            border-radius: 8px;
            margin-top: 1em;
            min-width: 300px; /* Ajusta este valor según prefieras */
        }
    `

    render() {
        return html`
            <div class="header">
                <img class="logo" src="images/TemporalLogo.png" alt="Temporal" />
                <div class="brand">
                    <span class="purple">Temporal</span><span> | </span>
                </div>
                <img class="logo" src="images/masOrangeLogo.jpg" alt="MasOrange" />
                <div class="brand">
                    <span class="mas">Mas</span><span class="orange">Orange</span>
                </div>
            </div>

            <div class="title">
                <h1>AI Prompts at Scale</h1>
                <h2>Managing AI Tasks with Temporal Workflows</h2>
            </div>

            <div class="explanation">
                <div class="intro-card">
                    Click the bot in the bottom right corner and start asking things like:
                </div>
                <div class="lists">
                    <div class="list-left">
                        <ul>
                            <li>Give me my accepted consents</li>
                            <li>Could you give me my rejected consents?</li>
                            <li>Could you give me my pending consents?</li>
                            <li>Show my subscriptions</li>
                        </ul>
                    </div>
                    <div class="list-right">
                        <ul>
                            <li>Do you know if there are some fraud investigations over any of my subscriptions?</li>
                            <li>I am thinking about changing my mobile, which ones do you recommend me? a cheap one please...</li>
                            <li>If in the above question the bot asks you for a model you can request a Nokia cheaper than 500€</li>
                        </ul>
                    </div>
                </div>
            </div>

            <div class="chatbot-design">
                <h3>Chatbot Design</h3>
                <img class="chatbot-design" src="images/chatbotDesign.png" alt="Chatbot design">
            </div>
        `
    }
}

customElements.define('demo-title', DemoTitle);