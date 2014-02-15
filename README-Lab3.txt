README: Mutual Exclusion Lab - Team 60.

Algorithm used:
We have made use of Maekawa's Algorithm for Distributed Mutual Exclusion. The algorithm mainly 
talks about sharing a distributed resource using a voting algorithm. Hence whenever a process 
wants access to the resource, it needs to get votes from all of it's group members and only then 
can it say that it has got access. Else it needs to wait till another process releses the lock
so that this process can access it. There are 2 types of messages namely 'Request' and 'Release' 
through which a process can gain or release access to the critical section. The Detailed algorithm 
is given below: 

/* Initialization for each process */
On initialization
	state := RELEASED;
	voted := FALSE;

/* Sending request for lock access */	
For process pi to enter the critical section
	state := WANTED;
	Multicast request to all processes in Vi;
	Wait until (number of replies received = K);
	state := HELD;
	
/* Receipt of Request by group members */
On receipt of a request from pi at pj
	if (state = HELD or voted = TRUE)
		then
		queue request from pi without replying;
	else
		send reply to pi;
		voted := TRUE;
	end if

/* Exiting critical section by sending a release */	
For pi to exit the critical section
	state := RELEASED;
	Multicast release to all processes in Vi;

/* Receipt of Release message at each group member */
On receipt of a release from pi at pj
	if (queue of requests is non-empty)
		then
		remove head of queue – from pk , say;
		send reply to pk;
		voted := TRUE;
	else
		voted := FALSE;
	end if

Group Construction Rules Violation Scenarios:

1) Violation of Pairwise non-null Intersection Property:
  This property says that there must be atleast one common element between any 2 groups in the system.

  Suppose we consider 2 groups say:
  Group A :  A, B, C   and    Group D : D, E, F.
  These 2 groups do not have any element in common. Hence if we apply Maekawa's Distributed Algorithm
  for Mutual Exclusion, if both A and D request for access to the Critical Section, A won't know about 
  D's request and D also won't know about A's request since they don't have a common element in both
  the groups. Hence, both A and D get access to the Critical Section which is wrong. Hence violation
  of this property results in an erroneous result.

2) Violation of Self-Contain Property:
  This property for all i, Pi is in G(i), i.e all Group i must contain element i.

  Consider the scenario where Group A doesn't contain A. Say Group A contains B, C and D.
  Also consider Group F contains A, F and G. If A requests for the critical section first. It gets 
  votes from B, C and D and takes the lock. Now consider F wants access to the critical section. It
  asks for a vote from A, F and G. A checks that it already has access to the critical section and hence
  adds itself to its queue and doesn't vote for F. Now when A releases the lock, he removes himself from
  the queues of B, C and D but doesn't remove himself from his own queue. Hence there is starvation for 
  F since it cannot acquire the lock at all.

3) Violation of Equal Effort property:
   This property says that all the groups must have equal number of elements equal to the square root
   of N. 
   Consider Group A has only one element A. Then it will violate the non-null intersection property with some
   groups not containing A.

4) Violation of Equal Responsibility property:
   This property states that an element must be contained in equal number of groups equal to the square root of
   N. Again, consider A is part of only one group say Group A. This again violates the non-null intersection 
   property since some groups won't have any elements in common.
  
