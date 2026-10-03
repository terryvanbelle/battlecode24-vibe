# Prompt record

User task prompts for this project, in chronological order, recorded verbatim
(spelling, punctuation and whitespace preserved). Append-only for the prompt
text: never edit an entry's words. Each entry is `## <number>. <date>` followed
by the prompt as typed (the date is the day the prompt was given, PDT).

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

## 7. 2026-09-30

task check

## 8. 2026-09-30

task check

## 9. 2026-09-30

I'd like to see field-score graphs in the repository, like the ones from bc20

## 10. 2026-09-30

That sounds reasonable

## 11. 2026-09-30

task check

## 12. 2026-09-30

task check

## 13. 2026-09-30

task check

## 14. 2026-09-30

task check

## 15. 2026-09-30

task check

## 16. 2026-09-30

task check

## 17. 2026-09-30

Are T1-T4 the only tactics you've observed in all the games you've played against all opponents?

## 18. 2026-09-30

As a standing order, I'd like you to update TACTICS.md every time you do a ladder run.  I'd like TACTICS to be comprehensive

## 19. 2026-09-30

Presumably many of the tactics are employed by many opponents, so eventually you'll stabilize on a fixed set of tactics that work against us, but there will be more then 4

## 20. 2026-09-30

task check

## 21. 2026-09-30

As you build up TACTICS.md, I'd like you to refer to the column adopting enemy tactics as "Adoption" rather than "Offense", and "Neutralization" rather then "Defense".  I'm afraid that the offense/defense dichotomy is confusing you, when actually it's more about adoption vs. neutralization

## 22. 2026-09-30

Keep the new dichotomy in mind when you update TACTICS.md

## 23. 2026-09-30

task check

## 24. 2026-09-30

task check

## 25. 2026-10-01

task check

## 26. 2026-10-01

task check

## 27. 2026-10-01

task check

## 28. 2026-10-01

task check

## 29. 2026-10-01

task check

## 30. 2026-10-01

task check

## 31. 2026-10-01

task check

## 32. 2026-10-01

task check

## 33. 2026-10-01

task check

## 34. 2026-10-01

task check

## 35. 2026-10-01

task check

## 36. 2026-10-01

Just want to double check:  Are you fully utilizing all the VM's CPUs?  Is there anything else you could do to improve VM game throughput?

## 37. 2026-10-01

OK, that's fine.  Just wanted to check that all the cores are engaged during a run

## 38. 2026-10-01

task check

## 39. 2026-10-01

task check

## 40. 2026-10-01

task check

## 41. 2026-10-01

task check

## 42. 2026-10-01

task check

## 43. 2026-10-01

task check

## 44. 2026-10-01

task check

## 45. 2026-10-01

task check

## 46. 2026-10-01

task check

## 47. 2026-10-01

task check

## 48. 2026-10-01

task check

## 49. 2026-10-01

task check

## 50. 2026-10-01

task check

## 51. 2026-10-01

task check

## 52. 2026-10-01

task check

## 53. 2026-10-01

task check

## 54. 2026-10-01

task check

## 55. 2026-10-01

Can you give me a summary of last night?

## 56. 2026-10-01

It sounds like TACTICS.md is not yielding the wins we'd hoped for.  Perhaps that's because each tactic on its own depends on other infrastructure and strategy to make it effective.  Can you investigate if there is a way to distinguish tactics that are "elementary" from ones that require a lot of infrastructure to work?  It might be more useful to focus on the basics, even if they're less effective, as building blocks to the more effective tactics

## 57. 2026-10-01

task check

## 58. 2026-10-01

task check

## 59. 2026-10-01

task check

## 60. 2026-10-01

task check

## 61. 2026-10-01

task check

## 62. 2026-10-01

task check

## 63. 2026-10-01

task check

## 64. 2026-10-01

task check

## 65. 2026-10-01

task check

## 66. 2026-10-01

I'm a bit concerned to hear that so many times you failed to reproduce the enemy's tactics, but went ahead with a ladder test anyways.  I thought your approach required you to reproduce the behavior you wanted in a test game before committing resources to the idea

## 67. 2026-10-01

From now on, make sure you honor Step 3.5 in TRAINING_ALGORITHM, and don't make any shortcuts

## 68. 2026-10-01

task check

## 69. 2026-10-01

task check

## 70. 2026-10-01

task check

## 71. 2026-10-01

task check

## 72. 2026-10-01

Can you tell me more about the idle-filler blocks?

## 73. 2026-10-01

Go ahead with the recommended change

## 74. 2026-10-01

task check

## 75. 2026-10-01

task check

## 76. 2026-10-01

task check

## 77. 2026-10-01

task check

## 78. 2026-10-01

task check

## 79. 2026-10-01

task check

## 80. 2026-10-01

task check

## 81. 2026-10-01

task check

## 82. 2026-10-02

task check

## 83. 2026-10-02

task check

## 84. 2026-10-02

Try again to delete the local replays.  I gave you new access on Github

## 85. 2026-10-02

task check

## 86. 2026-10-02

task check

## 87. 2026-10-02

task check

## 88. 2026-10-02

task check

## 89. 2026-10-02

It looks like we had some kind of reset.  Can you check to make sure everything's working ok?

## 90. 2026-10-02

/loop list

## 91. 2026-10-02

task check

## 92. 2026-10-02

You have permission to do what you think is right for deleting things that we don't need anymore, as long as you don't delete anything from github

## 93. 2026-10-02

task check

## 94. 2026-10-02

task check

## 95. 2026-10-02

task check

## 96. 2026-10-02

task check

## 97. 2026-10-02

task check

## 98. 2026-10-02

task check

## 99. 2026-10-02

task check

## 100. 2026-10-02

task check

## 101. 2026-10-02

task check

## 102. 2026-10-02

task check

## 103. 2026-10-02

task check

## 104. 2026-10-02

task check

## 105. 2026-10-02

task check

## 106. 2026-10-02

task check

## 107. 2026-10-02

task check

## 108. 2026-10-02

task check

## 109. 2026-10-02

task check

## 110. 2026-10-02

task check

## 111. 2026-10-02

We seem to be having some trouble making progress past g_iter1.  Can you take a step back, look at the big picture, and give a diagnosis for why that might be?

## 112. 2026-10-02

I think it's worth trying something new, and that's the point of these practice sessions, to hone our game.   I would like you to try it out, but keep statistics to evaluate whether it's an improvement

## 113. 2026-10-02

Regarding your point #4 about what's going wrong:  I agree with this diagnosis, and it's worth thinking about big picture ways to deal with it some more.  In an actual tournament, these designs will evolve gradually, and there's a warning for us to ensure we keep up with enemy tactics as they develop, devising strategies against them while they're still weak, rather than having to deal with them as a unit near the end of the tournament.  But we don't have that luxury in practice sessions

## 114. 2026-10-02

task check

## 115. 2026-10-02

This all sounds good.  Tomorrow morning, let's have a discussion about what's working and what's not with the new approach

## 116. 2026-10-02

task check

## 117. 2026-10-02

task check

## 118. 2026-10-02

task check

## 119. 2026-10-02

task check

## 120. 2026-10-02

I can answer your question about whether to try a second line of work in parallel:  I'd like you to pick a strategy and focus all your energies on it.  Your immediate goal is to achieve a single, unambiguous victory over a higher-ranked opponent on the ladder.  If you can achieve that, then you can try it more broadly.  Think of the bots above you as a wall that you're having difficulty getting through.  You only need to find a little crack to widen

## 121. 2026-10-02

task check

## 122. 2026-10-02

task check

## 123. 2026-10-02

task check

## 124. 2026-10-02

task check

## 125. 2026-10-02

Can you tell me more about our symmetry check?  I was under the impression we could figure it out via observations, without having to guess

## 126. 2026-10-02

task check

## 127. 2026-10-02

Yes, most definitely fix the symmetry check bug, and please do an audit to determine if anything else is broken.  Also add more tests to keep this from happening again.  This is basic stuff

## 128b. 2026-10-02

task check

## 129. 2026-10-02

task check

## 130. 2026-10-02

task check

## 131. 2026-10-02

task check

## 132. 2026-10-02

task check

## 133. 2026-10-02

task check

## 134. 2026-10-02

I very much want to know how g_iter1 with the symmetry fix does against g_iter1.  Can you also check whether our past rush attempts failed because the faulty symmetry checking sent them to the wrong spot?

## 135. 2026-10-02

(owner pasted a summary of symmetry checking from Google: fixed features such as walls and ruins, elimination as tiles are sensed, track checked locations to save bytecode, bitmasks, default to rotation and scout toward the centre)

## 136. 2026-10-02

task check

## 137. 2026-10-02

OK, thanks for all the hard work.  A general point:  The basics (symmetry, movement, combat, economy) are the foundation for everything else.  If they're not solid, nothing built on them will be effective.  Make sure they are and remain solid.  If you're having trouble making progress, check your basics.

## 138. 2026-10-02

Is it possible that g_iter1 wasn't relying much on symmetry calculations (perhaps because they were unreliable)?

## 139. 2026-10-02

task check

## 140. 2026-10-02

Excellent, glad to hear that the audit bore fruit.  Let's confirm the findings, and hopefully this should move the needle

## 141. 2026-10-02

task check

## 142. 2026-10-02

task check

## 143. 2026-10-03

task check

## 144. 2026-10-03

task check

## 145. 2026-10-03

task check

## 146. 2026-10-03

task check

## 147. 2026-10-03

task check

## 148. 2026-10-03

task check

## 149. 2026-10-03

This is fantastic, glad to hear you made progress.  I'm expecting you'll continue to work until our meeting, and beyond

## 150. 2026-10-03

I'm imagining that now that you've fixed some bugs, old approaches that you discarded are worth revisiting 

## 151. 2026-10-03

task check

## 152. 2026-10-03

task check

## 153. 2026-10-03

task check

## 154. 2026-10-03

I don't see g_iter2 in github.  Are you sure everything has been synced?

## 155. 2026-10-03

I see it in the src, but nothing in progress is updated

## 156. 2026-10-03

task check

## 157. 2026-10-03

It's fine to keep working on ColtG5, but you are now free to take on the next opponent.  ColtG5 is defeated, good work

## 158. 2026-10-03

From now on, if the opponent you've been working on ranks below you, you can choose a new opponent

## 159. 2026-10-03

Doesn't have to be the bot immediately above you if you think another one is better, but yes, the next one is often the best choice

## 160. 2026-10-03

task check

## 161. 2026-10-03

task check

## 162. 2026-10-03

task check
