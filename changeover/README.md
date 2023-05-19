Subject: Help me out here...

----------
From: Jim Tamer
Date: Tue, Feb 28, 2012 at 12:38 PM
To: Jeff Tamer

Hi Bud,

I have a problem that maybe you can help me figure out.  I'm trying to determine the most efficient way to order a series of tasks.  Each task contains one or more attributes or properties.  Let's call them "A", "B", "C", etc.  So, for example, one task might contain an "A" while another might contain an "A" and a "C" (denoted as "A C") and, still another, might contain a "B" and a "D" ("B D"). 

There are nine different attributes (A thru I) and each task can contain from 1 to 5 attributes (although there are only a few tasks that have more than 3 attributes and none with more than 5).  Although mathematically there are a zillion possible attribute combinations, for practical purposes there will never be more than about 35 unique attribute combinations to try and sequence. 

Here's the rule:  If, for example, I start with a task that has only "A" in it then I'm allowed to follow it with another task that has "A B" in it.  I can then follow that with "A B C" or "A B D" or "A B D F".  In other words I can immediately follow any task with another that contains all of the attributes that the previous task contained.

If you get to a point where there are no other tasks that meet our criterion then you have to insert a "changeover" tag into the task list and continue the sequence starting with a new task.  Continue until you have ordered all of the tasks such that you minimize the number of changeovers.  So to continue the example from the previous paragraph, The most efficient ordering would be one sequence of "A", "A B", "A B C" followed by a changeover and then followed by sequence "A B D" and "A B D F". 

Let me know how you would approach this problem or, better yet, give me a code snippet (java or pseudo-code) or two to build upon. 

I really appreciate any help you can give me big guy.

Love... Dad

----------
From: Jeff Tamer
Date: Tue, Feb 28, 2012 at 12:52 PM
To: Jim Tamer

Very interesting problem. I've analyzed it enough to see that using a greedy algorithm will sometimes get the wrong answer. Not sure yet if it's NP-complete. When you say "about 35 unique attribute combinations to try and sequence", does that mean the total number of tasks is around 35 at max? Or can there be multiple different tasks with identical attribute combinations sometimes, e.g. {"A", "A B", "A"}?

The goal here is to find the absolute minimum possible number of crossovers (and an ordering which produces that number), right?

----------
From: Jim Tamer
Date: Tue, Feb 28, 2012 at 12:55 PM
To: Jeff Tamer

Right, I need an ordering that produces the minimum number of changeovers.  The maximum number of tasks to sequence would be about 33 - 35 or so.

----------
From: Jeff Tamer
Date: Tue, Feb 28, 2012 at 1:19 PM
To: Jim Tamer

Yeah, this problem is definitely not trivial :)

I can certainly give you an optimized brute force solution, which is guaranteed to give the correct answer, but it would still be exponential time in the worst case. I'm afraid it'll probably be too slow for N=33, but it might be worthwhile to code it up and see just how slow it is. Depending on what a typical input set looks like, it might actually be acceptable. (Do you have one or two real-life inputs you could show me?)

I can also give you a heuristic-based polynomial-time solution, which will run fast but might not always find the absolute best answer. I haven't actually been able to come up with any counterexamples to my proposed algorithm here, but if I were to code up both versions (the brute force and the heuristic-based) and run them against each other, I'd be surprised if their answers didn't differ some small percentage of the time.

I'd be curious for more background information on this problem. Usually in real life, sets of tasks can be executed in parallel :)

----------
From: Jim Tamer
Date: Tue, Feb 28, 2012 at 4:35 PM
To: Jeff Tamer

You're correct in assuming  that there's a real life problem involved.  We run mixer batches of specialized ingredients to make custom spices.  Some of these have allergens.  If you're running a batch that contains allergens the cleanup (changeover) takes a very long time relative to a normal changeover.  That's why we want to minimize the changeovers.  

I don't care about process time to come up with a solution.  It could run as a scheduled task every night to prepare the next day's production schedule.

Give me a call when you have a couple of minutes to chat over an approach.  No rush - whenever you have time.  I'm  not in any hurry.

<3

----------
From: Jeff Tamer
Date: Wed, Feb 29, 2012 at 1:18 AM
To: Jim Tamer

OK, here's what I've got so far. I believe it always gets the right answer, and It seems to run in under 60 seconds even for very sadistic inputs. Give me a call tomorrow and let's talk -- I think there are still a few things I could do to make it faster.

```
>>> for i in range(10): print findBestOrdering("A, A B, A B C, A B D, A B D F")

["A", "A B", "A B C", '(changeover)', "A B D", "A B D F"]
["A", "A B", "A B C", '(changeover)', "A B D", "A B D F"]
["A B", "A B D", "A B D F", '(changeover)', "A", "A B C"]
["A B D", "A B D F", '(changeover)', "A", "A B", "A B C"]
["A", "A B D", "A B D F", '(changeover)', "A B", "A B C"]
["A B D", "A B D F", '(changeover)', "A", "A B", "A B C"]
["A B D", "A B D F", '(changeover)', "A", "A B", "A B C"]
["A", "A B", "A B D", "A B D F", '(changeover)', "A B C"]
["A B C", '(changeover)', "A", "A B", "A B D", "A B D F"]
["A", "A B D", "A B D F", '(changeover)', "A B", "A B C"]
```

Sorry for the messy code.

Love,
Jeff

----------
From: Jim Tamer
Date: Wed, Feb 29, 2012 at 5:10 AM
To: Jeff Tamer

Thanks so much.  I'll take a look when I get a chance.  Got meetings all day today so I probably won't get to it until tonight.  (hope I remember enough python to figure it out )  

----------
From: Jeff Tamer
Date: Thu, Mar 1, 2012 at 1:54 PM
To: Jim Tamer

Hey Dad,

Don't bother looking at the code that I sent you. I think there's an extremely clever way of solving this problem in polynomial time. I'll give it a shot and if it works, I'll send you some new code (in Java this time ;) )

Love,

Jeff

----------
From: Jeff Tamer
Date: Thu, Mar 1, 2012 at 10:55 PM
To: Jim Tamer

OK, check it out -- it's in Java, runs lightning fast, and I even left some comments in there for you ;)

Please shoot me an email or call me if you have any questions about the code. This version really is a million times better than the Python version.

Love,
Jeff

----------
From: Jeff Tamer
Date: Thu, Mar 1, 2012 at 11:21 PM
To: Jim Tamer

Version 2: I just removed several lines of unnecessary code.

----------
From: Jim Tamer
Date: Fri, Mar 2, 2012 at 12:09 PM
To: Jeff Tamer

Great.  I'll check it out over the weekend. Hope you have a good one. <3
