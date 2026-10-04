# Prompt record

User task prompts for this project, in chronological order, recorded verbatim
(spelling, punctuation and whitespace preserved). Append-only for the prompt
text: never edit an entry's words. Each entry is `## <number>. <date>` followed
by the prompt as typed (the date is the day the prompt was given, PDT).


> Prompts fired by the `/loop` ("task check") were removed on 2026-10-03 at the owner's request (PROMPTS 171-172); entry numbers are kept, so the numbering has gaps.

## 1. 2026-09-30

We’re going to build a world-class champion Battlecode bot.  Battlecode is a contest where the contestants implement bots to play against other bots in an arena.  Each year’s rules are different from prior years, but they all share some common features.  We have built bots for several prior years already (Github repositories, in order of attempt:  battlecode22-vibe, battlecode26-vibe, battlecode25-vibe, battlecode21-vibe, and battlecode20-vibe.  Each attempt was built on previous attempts, so weight the findings of later projects higher than earlier ones).  Read through the code and documentation for these projects thoroughly to learn what has already been done, and what has worked or not worked.  Pay particular attention to files called RESEARCH.md, LEARNINGS.md, DESIGN.md, METHOD.md, TRAINING_LOG.md, and TRAINING_ALGORITHM.md.  Also review all code and documentation from the github repository anicolao/bcenv.  Feel free to steal any code that might be useful to you.  The repository battlecode-vibe has a file called ADVICE.md, storing cross-year advice that will be useful to you.

We’re not participating in an actual Battlecode tournament, we’re practicing.  In an actual tournament, you would have two sources of data:  local fights against old versions of yourself, and online scrimmages against a variety of opponents in the tournament standings.  We can’t perfectly replicate this latter source of data, but we should try to get as close as possible. 
Please do a thorough check of the web, especially github, for competitor bots from the relevant year that are publicly accessible.  Download all of them to serve as your benchmark.  You may not read their code, but you are encouraged to analyze their tactics in games.  Simulate an ELO ladder based on the downloaded bots, where you challenge bots slightly better and slightly worse than you, using a random map with a random side.

Start a file called TACTICS.md.  If an opponent successfully uses a tactic against you, make a note of it there.  For each tactic, note progress towards developing your own version of that tactic (offensive) and developing ways to neutralize that tactic (defensive).  Both are valuable, but it’s better to develop the offense first, because then you can use your own bot to learn a defense.

When you have thoroughly read all recommended repositories, formulate your own TRAINING_ALGORITHM.md file.  This file should be concise, complete, and formulated in year-agnostic terms.  Please do not simply copy a previous year’s TRAINING_ALGORITHM file.  You are forbidden from reading port-mortems from the current year.  Post-mortems from any other year are fair game.

You should start by building a strong, robust foundation in the basics:  good economy management; ensuring that your bots can move freely and efficiently to their destinations; board exploration; exploiting map symmetries; effective combat (e.g. kite and strike); and ensuring no bytecode overruns.  Also invest time at the beginning in building a good code architecture and good tools for understanding everything that happens in a game replay file.  Generate graphs that illustrate your progress, and keep them up to date.  Write unit tests for the bot and all tooling, keep them up to date, and run them after every change.

Make sure that your attempts are a good combination of incremental tweaks and big swings.  If you get stuck for ideas, review principles that have worked in other years.  There will be times when no attempts are successful for a long period.  At those times, it’s important to keep trying new things, and to not give up.  If you believe that a complete rewrite will help, then you should do so.  At no time should you stop and wait for me to give you a new idea.

Starting with this one, save all of my prompts in a document called PROMPTS.md.

This year we will compete in Battlecode 2024.  Store all results in a new Github repository called battlecode24-vibe.  Download the rules and begin.

## 2. 2026-09-30

Try again

## 3. 2026-09-30

What is using the vm at the moment?

## 4. 2026-09-30

Try another zone

## 5. 2026-09-30

/loop 30m task check

## 6. 2026-09-30

If self-play can't show the value of a rush defence, maybe you should implement a rush offense.  Then you can use self-play to develop a rush defence

## 9. 2026-09-30

I'd like to see field-score graphs in the repository, like the ones from bc20

## 10. 2026-09-30

That sounds reasonable

## 17. 2026-09-30

Are T1-T4 the only tactics you've observed in all the games you've played against all opponents?

## 18. 2026-09-30

As a standing order, I'd like you to update TACTICS.md every time you do a ladder run.  I'd like TACTICS to be comprehensive

## 19. 2026-09-30

Presumably many of the tactics are employed by many opponents, so eventually you'll stabilize on a fixed set of tactics that work against us, but there will be more then 4

## 21. 2026-09-30

As you build up TACTICS.md, I'd like you to refer to the column adopting enemy tactics as "Adoption" rather than "Offense", and "Neutralization" rather then "Defense".  I'm afraid that the offense/defense dichotomy is confusing you, when actually it's more about adoption vs. neutralization

## 22. 2026-09-30

Keep the new dichotomy in mind when you update TACTICS.md

## 36. 2026-10-01

Just want to double check:  Are you fully utilizing all the VM's CPUs?  Is there anything else you could do to improve VM game throughput?

## 37. 2026-10-01

OK, that's fine.  Just wanted to check that all the cores are engaged during a run

## 55. 2026-10-01

Can you give me a summary of last night?

## 56. 2026-10-01

It sounds like TACTICS.md is not yielding the wins we'd hoped for.  Perhaps that's because each tactic on its own depends on other infrastructure and strategy to make it effective.  Can you investigate if there is a way to distinguish tactics that are "elementary" from ones that require a lot of infrastructure to work?  It might be more useful to focus on the basics, even if they're less effective, as building blocks to the more effective tactics

## 66. 2026-10-01

I'm a bit concerned to hear that so many times you failed to reproduce the enemy's tactics, but went ahead with a ladder test anyways.  I thought your approach required you to reproduce the behavior you wanted in a test game before committing resources to the idea

## 67. 2026-10-01

From now on, make sure you honor Step 3.5 in TRAINING_ALGORITHM, and don't make any shortcuts

## 72. 2026-10-01

Can you tell me more about the idle-filler blocks?

## 73. 2026-10-01

Go ahead with the recommended change

## 84. 2026-10-02

Try again to delete the local replays.  I gave you new access on Github

## 89. 2026-10-02

It looks like we had some kind of reset.  Can you check to make sure everything's working ok?

## 90. 2026-10-02

/loop list

## 92. 2026-10-02

You have permission to do what you think is right for deleting things that we don't need anymore, as long as you don't delete anything from github

## 111. 2026-10-02

We seem to be having some trouble making progress past g_iter1.  Can you take a step back, look at the big picture, and give a diagnosis for why that might be?

## 112. 2026-10-02

I think it's worth trying something new, and that's the point of these practice sessions, to hone our game.   I would like you to try it out, but keep statistics to evaluate whether it's an improvement

## 113. 2026-10-02

Regarding your point #4 about what's going wrong:  I agree with this diagnosis, and it's worth thinking about big picture ways to deal with it some more.  In an actual tournament, these designs will evolve gradually, and there's a warning for us to ensure we keep up with enemy tactics as they develop, devising strategies against them while they're still weak, rather than having to deal with them as a unit near the end of the tournament.  But we don't have that luxury in practice sessions

## 115. 2026-10-02

This all sounds good.  Tomorrow morning, let's have a discussion about what's working and what's not with the new approach

## 120. 2026-10-02

I can answer your question about whether to try a second line of work in parallel:  I'd like you to pick a strategy and focus all your energies on it.  Your immediate goal is to achieve a single, unambiguous victory over a higher-ranked opponent on the ladder.  If you can achieve that, then you can try it more broadly.  Think of the bots above you as a wall that you're having difficulty getting through.  You only need to find a little crack to widen

## 125. 2026-10-02

Can you tell me more about our symmetry check?  I was under the impression we could figure it out via observations, without having to guess

## 127. 2026-10-02

Yes, most definitely fix the symmetry check bug, and please do an audit to determine if anything else is broken.  Also add more tests to keep this from happening again.  This is basic stuff

## 134. 2026-10-02

I very much want to know how g_iter1 with the symmetry fix does against g_iter1.  Can you also check whether our past rush attempts failed because the faulty symmetry checking sent them to the wrong spot?

## 135. 2026-10-02

(owner pasted a summary of symmetry checking from Google: fixed features such as walls and ruins, elimination as tiles are sensed, track checked locations to save bytecode, bitmasks, default to rotation and scout toward the centre)

## 137. 2026-10-02

OK, thanks for all the hard work.  A general point:  The basics (symmetry, movement, combat, economy) are the foundation for everything else.  If they're not solid, nothing built on them will be effective.  Make sure they are and remain solid.  If you're having trouble making progress, check your basics.

## 138. 2026-10-02

Is it possible that g_iter1 wasn't relying much on symmetry calculations (perhaps because they were unreliable)?

## 140. 2026-10-02

Excellent, glad to hear that the audit bore fruit.  Let's confirm the findings, and hopefully this should move the needle

## 149. 2026-10-03

This is fantastic, glad to hear you made progress.  I'm expecting you'll continue to work until our meeting, and beyond

## 150. 2026-10-03

I'm imagining that now that you've fixed some bugs, old approaches that you discarded are worth revisiting 

## 154. 2026-10-03

I don't see g_iter2 in github.  Are you sure everything has been synced?

## 155. 2026-10-03

I see it in the src, but nothing in progress is updated

## 157. 2026-10-03

It's fine to keep working on ColtG5, but you are now free to take on the next opponent.  ColtG5 is defeated, good work

## 158. 2026-10-03

From now on, if the opponent you've been working on ranks below you, you can choose a new opponent

## 159. 2026-10-03

Doesn't have to be the bot immediately above you if you think another one is better, but yes, the next one is often the best choice

## 167. 2026-10-03

I see you took down Waffle overnight and are now working on cracking CyrilSharma, nice work!  How is the fight going?

## 168. 2026-10-03

Yes, cracking an opponent should override

## 169. 2026-10-03

So it seems that the audit that found those bugs yesterday was the high level breakthrough that allowed things to progress again.  Can you give me a prompt that would encapsulate what you did to get that audit?  I want to make sure it's carried over for future years

## 171. 2026-10-03

By the way, you don't need to log any prompt that comes from a /loop

## 172. 2026-10-03

Please remove the "task check" prompts

## 173. 2026-10-03

Yes, please update ADVICE.md, but please make it much much more concise, and eliminate any references to the current year and current year's documents.  Try to get it down to a small paragraph summary that contains only the most important information

## 174. 2026-10-03

How is the cracking of CyrilSharma progressing?

## 175. 2026-10-04

Nice to hear that the second audit also led to an improvement

## 176. 2026-10-04

How did things go last night?

## 177. 2026-10-04

If you get to a point where you've run out of lines of attack on CyrilSharma, feel free to open up the list of opponents to see if you can get purchase on another bot

## 178. 2026-10-04

I'm going to relax my restriction against choosing a map or side when playing the benchmarks.  You now can feel free to pick either or both if it helps
